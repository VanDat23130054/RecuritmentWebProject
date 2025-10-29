package com.java_web.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {


    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean female;
    private int yearOfBirth;
    private String industry;
    private String jobTitle;
    private String company;
    private String city;
    private String telephone;
    private String[] favorites;
    private String desiredPlatform;

    public User(String firstName, String lastName, String email, String password, boolean female, int yearOfBirth, String industry, String jobTitle, String company, String city, String telephone, String[] favorites, String desiredPlatform) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.female = female;
        this.yearOfBirth = yearOfBirth;
        this.industry = industry;
        this.jobTitle = jobTitle;
        this.company = company;
        this.city = city;
        this.telephone = telephone;
        this.favorites = favorites;
        this.desiredPlatform = desiredPlatform;
    }
}
