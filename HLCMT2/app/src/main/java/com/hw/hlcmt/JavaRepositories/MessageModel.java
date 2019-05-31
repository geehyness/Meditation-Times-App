package com.hw.hlcmt.JavaRepositories;

public class MessageModel {
    private String title;
    private String message;
    private String author;
    private String date;
    private String msgId;
    private String language;
    private int imageResource;
    private int week;
    private int year;

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public String getMsgId() {return msgId; }

    public void setMsgId(String msgId) {this.msgId = msgId; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public MessageModel(){}

    public MessageModel(String title, String message, String author, String date, int week, int year, int imageResource, String language) {
        this.imageResource = imageResource;
        this.title = title;
        this.message = message;
        this.author = author;
        this.date = date;
        this.week = week;
        this.language = language;
    }
}
