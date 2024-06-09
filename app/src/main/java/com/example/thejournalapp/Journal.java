package com.example.thejournalapp;

import com.google.firebase.Timestamp;

public class Journal {
    private String title;
    private String description;
    private String imgUrl;
    private Timestamp timeAdded;
    private String userId;
    private String userName;

    public Journal(String title, String description, String imgUrl, Timestamp timeAdded, String userId, String userName) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.timeAdded = timeAdded;
        this.userId = userId;
        this.userName = userName;
    }

    public Journal() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
