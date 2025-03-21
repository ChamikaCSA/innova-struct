package com.example.backend.model;

public class Project {
    private String id;
    private String image;
    private String title;
    private String description;
    private int year;

    public Project() {
    }

    public Project(String id, String image, String title, String description, int year) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}