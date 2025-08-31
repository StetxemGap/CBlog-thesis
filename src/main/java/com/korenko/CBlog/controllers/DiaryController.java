package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.DiaryCardDTO;
import com.korenko.CBlog.model.DiaryCards;
import com.korenko.CBlog.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DiaryController {
    @Autowired
    private DiaryService diaryService;

    @MessageMapping("/saveCard")
    public void saveCard(DiaryCardDTO cardDTO){
        DiaryCards savedCard = diaryService.saveCard(cardDTO);
    }

    @MessageMapping("/deleteCard")
    public void deleteCard(DiaryCardDTO cardDTO) {
        diaryService.deleteCard(cardDTO.getId());
    }
}
