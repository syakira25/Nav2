package com.example.jameedean.nav2.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class NoteModel {

    private String title, agency;
    private String description;
    private String imageUrl;
    private String drawingUrl;
    private String category;
    private long createdAt;

    public NoteModel() {}

    public NoteModel(String title, String agency, String description, String category, long createdAt) {
        this.title = title;
        this.agency = agency;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
    }

    public NoteModel(String title, String agency, String description, String imageUrl, String drawUrl, String category, long createdAt) {
        this.title = title;
        this.agency = agency;
        this.description = description;
        this.imageUrl = imageUrl;
        this.drawingUrl = drawUrl;
        this.category = category;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public String getDrawingUrl() {
        return drawingUrl;
    }

    public void setDrawingUrl(String drawingUrl) {
        this.drawingUrl = drawingUrl;
    }

    public String getCategory() {return category;}

    public void setCategory(String category){this.category=category;}

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
