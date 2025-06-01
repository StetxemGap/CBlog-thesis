package com.korenko.CBlog.service;

import com.korenko.CBlog.DTO.UsersDto;
import com.korenko.CBlog.model.UserPrincipal;
import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserInfoRepo;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Lazy
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    public Users findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<Users> findByActivationTrue() {
        return userRepo.findByActivationTrue();
    }

    public List<String> findAllActiveUsernames() {
        return userRepo.findAllActiveUsernames();
    }

    public List<Users> findAllUsers() {
        return userRepo.findAll();
    }

    public List<UsersInfo> findAllUsersInfo() {
        return userInfoRepo.findAll();
    }

    public void saveUserWithInfo(String username, String password,
                                 String firstname, String lastname,
                                 String position, Boolean admin) {

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsAdmin(admin);

        UsersInfo userInfo = new UsersInfo();
        userInfo.setFirstname(firstname);
        userInfo.setLastname(lastname);
        userInfo.setPosition(position);

        user.setUsersInfo(userInfo);
        userInfo.setUser(user);

        userRepo.save(user);
    }

    public void deleteUserById(String username) {
        Users user = userRepo.findByUsername(username);
        userRepo.delete(user);
    }

    public void deleteUserInfoById(String username) {
        Users tmp = userRepo.findByUsername(username);
        UsersInfo user = userInfoRepo.findByUserId(tmp.getId()).orElse(new UsersInfo());
        userInfoRepo.delete(user);
    }
}
