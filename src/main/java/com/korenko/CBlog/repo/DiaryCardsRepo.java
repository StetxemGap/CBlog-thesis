package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.DiaryCards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryCardsRepo extends JpaRepository<DiaryCards, Integer> {
}
