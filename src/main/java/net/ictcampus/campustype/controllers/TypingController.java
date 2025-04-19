package net.ictcampus.campustype.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TypingController {

    private static final Logger logger = LoggerFactory.getLogger(TypingController.class);

    private final UserRepository userRepository;
    private final TypingResultService typingResultService;
    private final JwtUtil jwtUtil;
    private final WordsService wordsService;
    private final Set<String> usedTokens = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public TypingController(UserRepository userRepository, TypingResultService typingResultService, JwtUtil jwtUtil, WordsService wordsService) {
        this.userRepository = userRepository;
        this.typingResultService = typingResultService;
        this.jwtUtil = jwtUtil;
        this.wordsService = wordsService;
    }

    @Operation(summary = "Start a typing test", description = "Generates a test sentence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test sentence generated"),
            @ApiResponse(responseCode = "400", description = "Invalid word count")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/start-test")
    public ResponseEntity<?> startTest(@RequestParam int wordCount) {
        if (wordCount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Word count must be positive");
        }
        try {
            List<Words> words = wordsService.getRandomWordsByAmount(wordCount);
            String sentence = words.stream()
                    .map(Words::getWord)
                    .collect(Collectors.joining(" "));
            return ResponseEntity.ok(new TestStartResponse(null, sentence));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating sentence: " + e.getMessage());
        }
    }

    @Operation(summary = "Generate test token", description = "Generates a signed test token when user starts typing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test token generated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/generate-test-token")
    public ResponseEntity<?> generateTestToken(@RequestBody TestTokenRequest request, HttpServletRequest httpRequest) {
        try {
            String authToken = httpRequest.getHeader("Authorization");
            if (authToken == null || !authToken.startsWith("Bearer ")) {
                logger.error("Missing or invalid Authorization header: {}", authToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            authToken = authToken.substring(7);
            logger.debug("Extracting userId from token: {}", authToken);
            Long userId = jwtUtil.extractUserId(authToken);
            long startTime = System.currentTimeMillis();
            String token = jwtUtil.generateTestToken(request.getSentence(), startTime, userId);
            logger.info("Generated test token for userId: {}", userId);
            return ResponseEntity.ok(new TestTokenResponse(token, startTime));
        } catch (JwtException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error generating token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating token: " + e.getMessage());
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
        if (usedTokens.contains(testToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test token already used");
        }
        try {
            String authToken = request.getHeader("Authorization");
            if (authToken == null || !authToken.startsWith("Bearer ")) {
                logger.error("Missing or invalid Authorization header: {}", authToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            authToken = authToken.substring(7);
            Long userId = jwtUtil.extractUserId(authToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            Claims claims;
            try {
                claims = jwtUtil.getClaims(testToken);
            } catch (SignatureException | MalformedJwtException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test token signature");
            } catch (ExpiredJwtException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test token has expired");
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test token");
            }

            Long startTime = claims.get("startTime", Long.class);
            String expectedSentence = claims.get("sentence", String.class);
            Long tokenUserId = claims.get("userId", Long.class);
            if (!tokenUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test token does not match user");
            }

            double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;
            if (!expectedSentence.equals(result.getSentence())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test token: sentence mismatch");
            }
            if (elapsedTime < 0 || elapsedTime > 600) { // Allow up to 10 minutes
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test duration");
            }

            if (!validateTypingResult(result)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or tampered typing results detected");
            }

            result.setUser(user);
            TypingResult savedResult = typingResultService.saveResult(result);
            usedTokens.add(testToken); // Mark token as used
            return ResponseEntity.ok(savedResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saving result: {}", e.getMessage());
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
            logger.error("Error retrieving result: {}", e.getMessage());
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

    // Helper class for /generate-test-token request
    private static class TestTokenRequest {
        private String sentence;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }
    }

    // Helper class for /generate-test-token response
    private static class TestTokenResponse {
        private final String token;
        private final long startTime;

        public TestTokenResponse(String token, long startTime) {
            this.token = token;
            this.startTime = startTime;
        }

        public String getToken() {
            return token;
        }

        public long getStartTime() {
            return startTime;
        }
    }
}