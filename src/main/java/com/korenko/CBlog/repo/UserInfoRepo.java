package com.korenko.CBlog.repo;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepo extends JpaRepository<UsersInfo, Integer> {
    UsersInfo findByUser(Users user);
}
