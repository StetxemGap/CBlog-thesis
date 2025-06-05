package com.korenko.CBlog.service;

import com.korenko.CBlog.DTO.DiaryCardDTO;
import com.korenko.CBlog.DTO.DiaryParagraphDTO;
import com.korenko.CBlog.model.DiaryCards;
import com.korenko.CBlog.model.DiaryParagraphs;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.repo.DiaryCardsRepo;
import com.korenko.CBlog.repo.DiaryParagraphsRepo;
import com.korenko.CBlog.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiaryService {

    @Autowired
    private DiaryCardsRepo diaryCardsRepo;

    @Autowired
    private DiaryParagraphsRepo diaryParagraphsRepo;

    @Autowired
    private UserRepo userRepo;

    public DiaryCards saveCard(DiaryCardDTO cardDTO) {
        DiaryCards card;
        if (cardDTO.getId() != null) {
            card = diaryCardsRepo.findById(cardDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Карточка не найдена"));
        } else {
            card = new DiaryCards();
            card.setDate(LocalDate.now());
        }

        Users user = userRepo.findById(cardDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        card.setTitle(cardDTO.getTitle());
        card.setUser(user);

        DiaryCards savedCard = diaryCardsRepo.save(card);

        if (cardDTO.getDiaryParagraphs() != null && !cardDTO.getDiaryParagraphs().isEmpty()) {

            if (savedCard.getId() != null) {
                List<Integer> existingParagraphIds = cardDTO.getDiaryParagraphs().stream()
                        .map(DiaryParagraphDTO::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (!existingParagraphIds.isEmpty()) {
                    diaryParagraphsRepo.deleteByCardIdAndIdNotIn(savedCard.getId(), existingParagraphIds);
                }
            }

            for (DiaryParagraphDTO paragraphDTO : cardDTO.getDiaryParagraphs()) {
                DiaryParagraphs paragraph = new DiaryParagraphs();

                if (paragraphDTO.getId() != null && savedCard.getId() != null) {
                    paragraph = diaryParagraphsRepo.findByIdAndCardId(paragraphDTO.getId(), savedCard.getId())
                            .orElse(new DiaryParagraphs());
                }

                paragraph.setContent(paragraphDTO.getContent());
                paragraph.setIsReady(paragraphDTO.getIsReady() != null ? paragraphDTO.getIsReady() : false);
                paragraph.setCard(savedCard);

                diaryParagraphsRepo.save(paragraph);
            }
        }

        return savedCard;
    }

    public void deleteCard(Integer cardId) {
        DiaryCards card = diaryCardsRepo.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Карточка не найдена"));

        diaryCardsRepo.delete(card);
    }

}
