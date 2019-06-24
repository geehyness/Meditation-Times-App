package com.hw.hlcmt.JavaRepositories.Models;

public class UserModel {
    private String name;
    private String email;
    private String userId;
    private boolean English = true;
    private boolean Siswati = false;
    private boolean Writer = false;
    private boolean Admin = false;

    public String getName() {
        return name;
    }
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public boolean isSiswati() { return Siswati; }
    public boolean isEnglish() { return English; }
    public boolean isWriter() { return Writer; }
    public boolean isAdmin() { return Admin; }

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
    public void setWriter(boolean writer) { Writer = writer; }
    public void setAdmin(boolean admin) { Admin = admin; }

    public UserModel(){}

    public UserModel(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}