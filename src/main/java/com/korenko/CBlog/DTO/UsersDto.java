package com.korenko.CBlog.DTO;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersContact;
import com.korenko.CBlog.model.UsersInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UsersDto {

    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String position;
    private LocalDate dob;
    private String gender;
    private String city;
    private String street;
    private String officeFloor;
    private String officeNumber;
    private String photoPath;
    private Boolean activation;
    private Boolean admin;
    private String phoneNumber;
    private String email;
    private String VKid;
    private String TelegramUsername;

    public UsersDto(Users user) {
        this.username = user.getUsername();

        UsersInfo userInfo = user.getUsersInfo();
        UsersContact usersContact = user.getUsersContact();
        this.firstName = userInfo.getFirstname();
        this.lastName = userInfo.getLastname();
        this.position = userInfo.getPosition();
        this.dob = userInfo.getDob();
        this.gender = userInfo.getGender();
        this.city = userInfo.getCity();
        this.street = userInfo.getStreet();
        this.officeFloor = userInfo.getOfficeFloor();
        this.officeNumber = userInfo.getOfficeNumber();
        this.photoPath = userInfo.getPhotoPath();
        this.activation = user.getActivation();
        this.admin = user.getIsAdmin();
        this.phoneNumber = usersContact.getPhoneNumber();
        this.email = usersContact.getEmail();
        this.VKid = usersContact.getVKid();
        this.TelegramUsername = usersContact.getTelegramUsername();
    }

    public UsersDto(UsersInfo user) {
        this.id = user.getUser().getId();
        this.username = user.getUser().getUsername();
        this.firstName = user.getFirstname();
        this.lastName = user.getLastname();
        this.position = user.getPosition();
        this.activation = user.getUser().getActivation();
        this.admin = user.getUser().getIsAdmin();
    }

    public UsersDto(String username, String firstName, String lastName, String photoPath) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoPath = photoPath;
    }
}
