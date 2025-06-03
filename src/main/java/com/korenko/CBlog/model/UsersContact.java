package com.korenko.CBlog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name="users_contact")
@Setter
@Getter

public class UsersContact implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String phoneNumber;
    private String email;
    private String VKid;
    private String TelegramUsername;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
