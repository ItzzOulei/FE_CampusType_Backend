package net.ictcampus.campustype.controllers;

import net.ictcampus.campustype.models.Words;
import net.ictcampus.campustype.services.WordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WordsController {

    private final WordsService wordsService;

    @Autowired
    public WordsController(WordsService wordsService) {
        this.wordsService = wordsService;
    }

    @Operation(summary = "Get random words", description = "Gets all words from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of random words retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/words")
    public ResponseEntity<?> getWords() {
        try {
            List<Words> words = wordsService.getRandomWords();
            return ResponseEntity.ok(words);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Get random words", description = "Retrieves a specified amount of random words")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of random words retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid amount")
    })
    @GetMapping("/words/{wordsAmount}")
    public ResponseEntity<?> getWords(@PathVariable Integer wordsAmount) {
        try {
            List<Words> words = wordsService.getRandomWordsByAmount(wordsAmount);
            return ResponseEntity.ok(words);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}