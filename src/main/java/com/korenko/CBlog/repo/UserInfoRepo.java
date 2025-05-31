package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepo extends JpaRepository<UsersInfo, Integer> {

    @Query("SELECT DISTINCT ui.position FROM UsersInfo ui WHERE ui.position IS NOT NULL")
    List<String> findAllPositions();

    @Query("SELECT DISTINCT ui.city FROM UsersInfo ui WHERE ui.city IS NOT NULL")
    List<String> findAllCities();

    Optional<UsersInfo> findByUserId(Integer userId);
}
