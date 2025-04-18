package net.ictcampus.campustype.repositories;

import net.ictcampus.campustype.models.Words;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordsRepository extends JpaRepository<Words, String> {
    @Query(value = "SELECT * FROM words ORDER BY RAND() LIMIT :amount", nativeQuery = true)
    List<Words> findRandomWords(int amount);
}