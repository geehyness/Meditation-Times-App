package com.hw.hlcmt.JavaRepositories;

public class Comment {
    String messageId;
    String userName;
    String comment;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Comment() {}

    public Comment(String messageId, String userName, String comment) {
        this.messageId = messageId;
        this.userName = userName;
        this.comment = comment;
    }
}
