package com.korenko.CBlog.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryParagraphDTO {
    private Integer id;
    private String content;
    private Boolean isReady;
    private Integer cardId;
}
