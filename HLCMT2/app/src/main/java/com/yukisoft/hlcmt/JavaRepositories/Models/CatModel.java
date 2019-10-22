package com.yukisoft.hlcmt.JavaRepositories.Models;

public class CatModel {
    private String id;
    private String imageURL;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatModel() {
    }

    public CatModel(String imageURL, String name) {
        this.imageURL = imageURL;
        this.name = name;
    }
}
