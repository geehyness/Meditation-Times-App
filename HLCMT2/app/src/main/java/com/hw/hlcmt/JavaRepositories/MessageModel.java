package com.hw.hlcmt.JavaRepositories;

public class MessageModel {
    private String title, message, author;
    private String date;
    private int imageResource, week;

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

    public MessageModel(String title, String message, String author, String date, int week, int imageResource) {
        this.imageResource = imageResource;
        this.title = title;
        this.message = message;
        this.author = author;
        this.date = date;
        this.week = week;
    }
}
