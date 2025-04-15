package com.korenko.CBlog.DTO;

import com.korenko.CBlog.model.Users;
import com.korenko.CBlog.model.UsersInfo;

import java.sql.Date;

public class UsersDto {

    private String username;
    private String firstName;
    private String lastName;
    private String position;
    private Date DoB;
    private String gender;
    private String city;
    private String street;
    private String officeFloor;
    private String officeNumber;

    public UsersDto(Users user) {
        this.username = user.getUsername();

        UsersInfo userInfo = user.getUsersInfo();
        this.firstName = userInfo.getFirstName();
        this.lastName = userInfo.getLastName();
        this.position = userInfo.getPosition();
        this.DoB = userInfo.getDoB();
        this.gender = userInfo.getGender();
        this.city = userInfo.getCity();
        this.street = userInfo.getStreet();
        this.officeFloor = userInfo.getOfficeFloor();
        this.officeNumber = userInfo.getOfficeNumber();
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public Date getDoB() {
        return DoB;
    }

    public String getGender() {
        return gender;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getOfficeFloor() {
        return officeFloor;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }
}
