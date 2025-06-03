package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.PasswordRequests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRequestsRepo extends JpaRepository<PasswordRequests, Integer> {
    void deleteByUsername(String username);
}
