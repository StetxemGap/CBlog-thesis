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
    private String photoPath;

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
        this.photoPath = userInfo.getPhotoPath();
    }

    public UsersDto(String username, String firstName, String lastName, String photoPath) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoPath = photoPath;
    }

    public UsersDto() {
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDoB(Date doB) {
        DoB = doB;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setOfficeFloor(String officeFloor) {
        this.officeFloor = officeFloor;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
