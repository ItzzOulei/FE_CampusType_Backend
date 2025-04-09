package net.ictcampus.campustype.repositories;

import net.ictcampus.campustype.models.TypingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypingResultRepository extends JpaRepository<TypingResult, Long> {
    @Query("SELECT tr FROM TypingResult tr ORDER BY tr.wpm DESC")
    List<TypingResult> findTopResults();
}