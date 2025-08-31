package com.korenko.CBlog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Setter
@Getter
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;
    @Column(nullable = false)
    private Boolean isPart = false;
    @Column(nullable = false)
    private Boolean isLastPart = false;
    private Integer partNumber;
    @Column(nullable = false)
    private Boolean isFile;
    @Column(nullable = false)
    private Boolean isRead = false;
}
