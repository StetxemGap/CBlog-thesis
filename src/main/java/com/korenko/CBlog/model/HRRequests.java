package com.korenko.CBlog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="hr_requests")
@Setter
@Getter
public class HRRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstname;
    private String lastname;
    private String position;
    private LocalDate hiringDate;
    private Boolean isDelete = false;
}
