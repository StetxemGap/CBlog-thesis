package com.korenko.CBlog.service;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersContact;
import com.korenko.CBlog.model.UsersInfo;
import com.korenko.CBlog.repo.UserContactRepo;
import com.korenko.CBlog.repo.UserInfoRepo;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ActivationService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private UserContactRepo userContactRepo;

    @Autowired
    private FileStorageService fileStorageService;

    public void activateUser(Integer userId, UsersInfo usersInfo, UsersContact usersContact, MultipartFile photoFile) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActivation(true);

        UsersInfo existingInfo = userInfoRepo.findByUserId(userId)
                .orElse(new UsersInfo());

        existingInfo.setFirstname(usersInfo.getFirstname());
        existingInfo.setLastname(usersInfo.getLastname());
        existingInfo.setPosition(usersInfo.getPosition());
        existingInfo.setDob(usersInfo.getDob());
        existingInfo.setGender(usersInfo.getGender());
        existingInfo.setCity(usersInfo.getCity());
        existingInfo.setStreet(usersInfo.getStreet());
        existingInfo.setOfficeFloor(usersInfo.getOfficeFloor());
        existingInfo.setOfficeNumber(usersInfo.getOfficeNumber());

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileStorageService.storeFile(photoFile, userId);
            existingInfo.setPhotoPath(photoPath);
        }

        existingInfo.setUser(user);
        userInfoRepo.save(existingInfo);

        UsersContact newUsersContact = userContactRepo.findByUserId(userId)
                .orElse(new UsersContact());

        newUsersContact.setPhoneNumber(usersContact.getPhoneNumber());
        newUsersContact.setEmail(usersContact.getEmail());
        newUsersContact.setVKid(usersContact.getVKid());
        newUsersContact.setTelegramUsername(usersContact.getTelegramUsername());
        newUsersContact.setUser(user);
        userContactRepo.save(newUsersContact);
    }
}
