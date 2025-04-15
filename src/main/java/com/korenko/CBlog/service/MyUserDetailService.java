package com.korenko.CBlog.service;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UserPrincipal;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserInfoRepo;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserInfoRepo userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username);

        if (user == null) {
            System.out.println("Пользователь не найден");
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return new UserPrincipal(user);
    }

    public UsersDto getUserProfile(String username) {
        Users user = userRepo.findByUsername(username);
        return new UsersDto(user);
    }

    @Transactional
    public UsersInfo saveUsersInfo(UsersInfo usersInfo) {
        return userInfoRepo.save(usersInfo);
    }
}
