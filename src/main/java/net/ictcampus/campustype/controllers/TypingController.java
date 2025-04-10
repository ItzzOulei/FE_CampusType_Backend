package net.ictcampus.campustype.controllers;

import jakarta.servlet.http.HttpServletRequest;
import net.ictcampus.campustype.models.TypingResult;
import net.ictcampus.campustype.models.User;
import net.ictcampus.campustype.repositories.UserRepository;
import net.ictcampus.campustype.security.JwtUtil;
import net.ictcampus.campustype.services.TypingResultService;
import net.ictcampus.campustype.services.WordsService;
import net.ictcampus.campustype.models.Words;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TypingController {

    private final UserRepository userRepository;
    private final TypingResultService typingResultService;
    private final JwtUtil jwtUtil;
    private final WordsService wordsService;

    @Autowired
    public TypingController(UserRepository userRepository, TypingResultService typingResultService, JwtUtil jwtUtil, WordsService wordsService) {
        this.userRepository = userRepository;
        this.typingResultService = typingResultService;
        this.jwtUtil = jwtUtil;
        this.wordsService = wordsService;
    }

    @Operation(summary = "Start a typing test", description = "Generates a test sentence and returns a signed token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test started with token"),
            @ApiResponse(responseCode = "400", description = "Invalid word count")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/start-test")
    public ResponseEntity<?> startTest(@RequestParam int wordCount, HttpServletRequest request) {
        if (wordCount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Word count must be positive");
        }
        try {
            List<Words> words = wordsService.getRandomWordsByAmount(wordCount);
            String sentence = words.stream()
                    .map(Words::getWord)
                    .collect(Collectors.joining(" "));
            long startTime = System.currentTimeMillis();
            String token = jwtUtil.generateTestToken(sentence, startTime);
            return ResponseEntity.ok(new TestStartResponse(token, sentence));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating sentence: " + e.getMessage());
        }
    }

    @Operation(summary = "Save typing result", description = "Saves a new typing result after validating token and calculations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Typing result saved"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID, token, or tampered results"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/results")
    public ResponseEntity<?> saveResult(@RequestBody TypingResult result, @RequestHeader("Test-Token") String testToken, HttpServletRequest request) {
        try {
            String authToken = request.getHeader("Authorization").substring(7);
            Long userId = jwtUtil.extractUserId(authToken);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            Long startTime = jwtUtil.extractStartTime(testToken);
            String expectedSentence = jwtUtil.extractSentence(testToken);
            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
            if (!expectedSentence.equals(result.getSentence()) || Math.abs(elapsedTime - result.getTime()) > 2.0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test token or timing mismatch");
            }

            if (!validateTypingResult(result)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or tampered typing results detected");
            }

            result.setUser(user);
            TypingResult savedResult = typingResultService.saveResult(result);
            return ResponseEntity.ok(savedResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving result: " + e.getMessage());
        }
    }

    private boolean validateTypingResult(TypingResult result) {
        String sentence = result.getSentence();
        String userInput = result.getUserInput();
        double time = result.getTime();
        int submittedWpm = result.getWpm();
        double submittedAccuracy = result.getAccuracy();
        int submittedCpm = result.getCpm();
        int submittedWords = result.getWords();

        if (sentence == null || userInput == null || sentence.length() != userInput.length() || time <= 0) {
            return false;
        }

        int correct = 0;
        for (int i = 0; i < sentence.length(); i++) {
            if (sentence.charAt(i) == userInput.charAt(i)) {
                correct++;
            }
        }
        double accuracyDecimal = (double) correct / sentence.length();
        double calculatedAccuracy = Math.round(accuracyDecimal * 100 * 100) / 100.0;

        double timeInMinutes = time / 60.0;
        int calculatedCpm = (int) Math.round(correct / timeInMinutes);

        String[] words = sentence.trim().split("\\s+");
        int wordCount = words.length;
        if (wordCount != submittedWords) {
            return false;
        }
        int calculatedWpm = (int) Math.round((wordCount / timeInMinutes) * accuracyDecimal);

        final int INT_TOLERANCE = 2;
        final double DOUBLE_TOLERANCE = 0.5;

        return Math.abs(calculatedWpm - submittedWpm) <= INT_TOLERANCE &&
                Math.abs(calculatedAccuracy - submittedAccuracy) <= DOUBLE_TOLERANCE &&
                Math.abs(calculatedCpm - submittedCpm) <= INT_TOLERANCE;
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Get user typing results", description = "Retrieves all typing results for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of typing results retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/results/{userId}")
    public ResponseEntity<?> getUserResults(@PathVariable Long userId) {
        try {
            List<TypingResult> results = typingResultService.getUserResults(userId);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Get leaderboard", description = "Retrieves the leaderboard with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/leaderboard")
    public ResponseEntity<List<TypingResult>> getLeaderboard(
            @RequestParam(defaultValue = "wpm") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) Integer minWpm,
            @RequestParam(required = false) Integer words) {
        try {
            List<TypingResult> leaderboard = typingResultService.getLeaderboard(sortBy, order, limit, minWpm, words);
            return ResponseEntity.ok(leaderboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Get leaderboard no filter", description = "Retrieves all Leaderboard data, grouped by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved")
    })
    @GetMapping("/leaderboard/all")
    public List<TypingResult> getAllTypingResults() {
        return typingResultService.getAllTypingResults();
    }

    @Operation(summary = "Get typing result by run ID", description = "Retrieves a specific typing result by its run ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Typing result retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid run ID"),
            @ApiResponse(responseCode = "404", description = "Typing result not found")
    })
    @GetMapping("/run/{runId}")
    public ResponseEntity<?> getRunResult(@PathVariable Long runId) {
        try {
            TypingResult result = typingResultService.getResultById(runId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving result: " + e.getMessage());
        }
    }

    // Helper class for /start-test response
    private static class TestStartResponse {
        private final String token;
        private final String sentence;

        public TestStartResponse(String token, String sentence) {
            this.token = token;
            this.sentence = sentence;
        }

        public String getToken() {
            return token;
        }

        public String getSentence() {
            return sentence;
        }
    }
}