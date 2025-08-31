package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UsersInfo u WHERE " +
            "u.firstname = :firstname AND " +
            "u.lastname = :lastname AND " +
            "u.position = :position AND " +
            "u.hiringDate = :hiringDate")
    boolean existsByFourParameters(
            @Param("firstname") String firstname,
            @Param("lastname") String lastname,
            @Param("position") String position,
            @Param("hiringDate") LocalDate hiringDate);

    @Query("SELECT u FROM UsersInfo u WHERE " +
            "u.firstname = :firstname AND " +
            "u.lastname = :lastname AND " +
            "u.position = :position AND " +
            "u.hiringDate = :hiringDate")
    Optional<UsersInfo> findUserByFourParameters(
            @Param("firstname") String firstname,
            @Param("lastname") String lastname,
            @Param("position") String position,
            @Param("hiringDate") LocalDate hiringDate);
}
