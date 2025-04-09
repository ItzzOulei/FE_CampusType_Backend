package net.ictcampus.campustype.services;

import net.ictcampus.campustype.models.TypingResult;
import net.ictcampus.campustype.repositories.TypingResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Add this import

@Service
public class TypingResultService {

    private final TypingResultRepository resultRepository;

    @Autowired
    public TypingResultService(TypingResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public TypingResult saveResult(TypingResult result) {
        return resultRepository.save(result);
    }

    public TypingResult getResultById(Long runId) {
        return resultRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Typing result not found with ID: " + runId));
    }

    public List<TypingResult> getUserResults(Long userId) {
        return resultRepository.findAll().stream()
                .filter(result -> result.getUser().getId().equals(userId))
                .toList();
    }

    public List<TypingResult> getLeaderboard(String sortBy, String order, int limit, Integer minWpm, Integer words) {
        List<TypingResult> allResults = resultRepository.findTopResults(); // All results sorted by WPM DESC

        if (minWpm != null) {
            allResults = allResults.stream()
                    .filter(result -> result.getWpm() >= minWpm)
                    .toList();
        }

        if (words != null) {
            allResults = allResults.stream()
                    .filter(result -> result.getWords() == words)
                    .toList();
        }

        List<TypingResult> topResults = allResults.stream()
                .collect(Collectors.groupingBy(
                        TypingResult::getUser,
                        Collectors.maxBy(Comparator.comparingInt(TypingResult::getWpm))
                ))
                .values()
                .stream()
                .map(Optional::get)
                .toList();

        Comparator<TypingResult> comparator;
        switch (sortBy.toLowerCase()) {
            case "accuracy":
                comparator = Comparator.comparingDouble(TypingResult::getAccuracy);
                break;
            case "time":
                comparator = Comparator.comparingDouble(TypingResult::getTime);
                break;
            case "words":
                comparator = Comparator.comparingInt(TypingResult::getWords);
                break;
            case "wpm":
            default:
                comparator = Comparator.comparingInt(TypingResult::getWpm);
                break;
        }

        if ("asc".equalsIgnoreCase(order)) {
            topResults = topResults.stream()
                    .sorted(comparator)
                    .toList();
        } else {
            topResults = topResults.stream()
                    .sorted(comparator.reversed())
                    .toList();
        }

        return topResults.size() > limit ? topResults.subList(0, limit) : topResults;
    }

    public List<TypingResult> getAllTypingResults() {
        return resultRepository.findAll();
    }
}