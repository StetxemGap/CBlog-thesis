package com.korenko.CBlog.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageDTO {
    private Integer id;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isRead;
}
