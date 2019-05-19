package com.hw.hlcmt.JavaRepositories;

public class UserModel {
    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String name;
    private String email;
    private String userType;

    public String getUserIdDoc() {
        return userIdDoc;
    }

    public void setUserIdDoc(String userIdDoc) {
        this.userIdDoc = userIdDoc;
    }

    private String userIdDoc;
    private String userId;

    public UserModel(){}

    public UserModel(String userId, String name, String email, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }
}