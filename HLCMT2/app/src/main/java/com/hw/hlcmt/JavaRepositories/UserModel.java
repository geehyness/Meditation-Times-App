package com.hw.hlcmt.JavaRepositories;

public class UserModel {
    private String name;
    private String email;
    private String userType;
    private String userId;

    public String getName() {
        return name;
    }
    public String getUserType() {
        return userType;
    }
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setName(String name) {
        this.name = name;
    }

    public UserModel(){}

    public UserModel(String userId, String name, String email, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }
}