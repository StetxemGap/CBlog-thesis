package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.UsersContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserContactRepo extends JpaRepository<UsersContact, Integer> {
    Optional<UsersContact> findByUserId(Integer userId);

    UsersContact findByEmail(String email);
}
