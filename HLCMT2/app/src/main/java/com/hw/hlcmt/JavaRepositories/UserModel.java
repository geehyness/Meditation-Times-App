package com.hw.hlcmt.JavaRepositories;

public class UserModel {
    private String name;
    private String email;
    private UserType userType;
    private String userId;
    private boolean English = true;
    private boolean Siswati = false;

    public String getName() {
        return name;
    }
    public UserType getUserType() {
        return userType;
    }
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public boolean isSiswati() { return Siswati; }
    public boolean isEnglish() { return English; }

    public void setUserType(UserType userType) {
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
    public void setSiswati(boolean siswati) { Siswati = siswati; }
    public void setEnglish(boolean english) { English = english; }

    public UserModel(){}

    public UserModel(String userId, String name, String email, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }
}