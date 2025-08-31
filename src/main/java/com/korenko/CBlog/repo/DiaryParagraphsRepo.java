package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.DiaryParagraphs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryParagraphsRepo extends JpaRepository<DiaryParagraphs, Integer> {
    @Modifying
    @Query("DELETE FROM DiaryParagraphs p WHERE p.card.id = :cardId AND p.id NOT IN :ids")
    void deleteByCardIdAndIdNotIn(@Param("cardId") Integer cardId, @Param("ids") List<Integer> ids);

    Optional<DiaryParagraphs> findByIdAndCardId(Integer id, Integer cardId);
}
