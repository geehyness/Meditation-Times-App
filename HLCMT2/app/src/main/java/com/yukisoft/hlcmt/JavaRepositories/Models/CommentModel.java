package com.yukisoft.hlcmt.JavaRepositories.Models;

public class CommentModel {
    private String messageId;
    private String userId;
    private String comment;
    private String commentId;
    private String userName = "Unknown";
    private int number;

    public int getNumber() { return number; }

    public void setNumber(int number) { this.number = number; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getCommentId() { return commentId; }

    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentModel() {}

    public CommentModel(int number, String messageId, String userId, String userName, String comment) {
        this.number = number;
        this.messageId = messageId;
        this.userId = userId;
        this.comment = comment;
        this.userName = userName;
    }
}
