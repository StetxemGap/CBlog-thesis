package com.korenko.CBlog.service;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserInfoRepo;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivationService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserInfoRepo userInfoRepo;

    public void activateUser(Integer userId, UsersInfo usersInfo) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActivation(true);


        usersInfo.setUser(user);
        userInfoRepo.save(usersInfo);
    }
}
