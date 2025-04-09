package net.ictcampus.campustype.services;

import net.ictcampus.campustype.models.Words;
import net.ictcampus.campustype.repositories.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordsService {

    @Autowired
    private WordsRepository wordsRepository;

    public List<Words> getRandomWordsByAmount(int amount) {
        long totalWords = wordsRepository.count();
        if (amount <= 0 || amount > totalWords) {
            throw new IllegalArgumentException("Amount must be between 1 and " + totalWords);
        }
        return wordsRepository.findRandomWords(amount);
    }

    public List<Words> getRandomWords() {
        long totalWords = wordsRepository.count();
        if (totalWords <= 0) {
            throw new IllegalArgumentException("Total words must be greater than 0");
        }
        return wordsRepository.findRandomWords((int) totalWords);
    }
}