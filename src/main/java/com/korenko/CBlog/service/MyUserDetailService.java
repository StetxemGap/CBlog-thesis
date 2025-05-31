package com.korenko.CBlog.service;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UserPrincipal;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserInfoRepo;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username);
        return new UserPrincipal(user);
    }

    public UsersDto getUserProfile(String username) {
        Users user = userRepo.findByUsername(username);
        return new UsersDto(user);
    }

    public List<String> findAllPositions() {
        return userInfoRepo.findAllPositions();
    }

    public List<String> findAllCities() {
        return userInfoRepo.findAllCities();
    }
}
