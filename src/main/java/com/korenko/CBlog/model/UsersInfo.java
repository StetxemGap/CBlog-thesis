package com.korenko.CBlog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name="users_info")
@Setter
@Getter
public class UsersInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstname;
    private String lastname;
    private String position;
    private LocalDate dob;
    private String gender;
    private String city;
    private String street;
    private String officeFloor;
    private String officeNumber;
    private String photoPath;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
