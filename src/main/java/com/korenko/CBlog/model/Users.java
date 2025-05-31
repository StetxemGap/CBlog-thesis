package com.korenko.CBlog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name="users")
@Setter
@Getter
public class Users implements Serializable {
    @Id
    private int id;
    private String username;
    private String password;
    private Boolean activation;
    @Column(nullable = false)
    private Boolean isAdmin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UsersInfo usersInfo;

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", activation=" + activation +
                '}';
    }
}
