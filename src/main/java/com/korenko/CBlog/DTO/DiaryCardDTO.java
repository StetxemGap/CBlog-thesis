package com.korenko.CBlog.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DiaryCardDTO {
    private Integer id;
    private String title;
    private LocalDate date;
    private List<DiaryParagraphDTO> diaryParagraphs;
    private Integer userId;
}
