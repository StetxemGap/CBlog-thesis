package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByUsername(String username);

    List<Users>  findByActivationTrue();
    @Query("SELECT u.username FROM Users u WHERE u.activation = true")
    List<String> findAllActiveUsernames();
}
