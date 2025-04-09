package net.ictcampus.campustype.controllers;

import jakarta.servlet.http.HttpServletRequest;
import net.ictcampus.campustype.models.TypingResult;
import net.ictcampus.campustype.models.User;
import net.ictcampus.campustype.repositories.UserRepository;
import net.ictcampus.campustype.security.JwtUtil;
import net.ictcampus.campustype.services.TypingResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TypingController {

    private final UserRepository userRepository;
    private final TypingResultService typingResultService;
    private final JwtUtil jwtUtil;

    @Autowired
    public TypingController(UserRepository userRepository, TypingResultService typingResultService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.typingResultService = typingResultService;
        this.jwtUtil = jwtUtil;
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

    @Operation(summary = "Save typing result", description = "Saves a new typing result for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Typing result saved"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/results")
    public ResponseEntity<?> saveResult(@RequestBody TypingResult result, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Long userId = jwtUtil.extractUserId(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            result.setUser(user);

            TypingResult savedResult = typingResultService.saveResult(result);
            return ResponseEntity.ok(savedResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving result: " + e.getMessage());
        }
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

    @Operation(summary = "Get leaderboard", description = "Retrieves the leaderboard with optional filters: sortBy (wpm, accuracy, time, words), order (asc, desc), limit (number of results), minWpm (minimum WPM filter), words (exact word count filter)")
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
}