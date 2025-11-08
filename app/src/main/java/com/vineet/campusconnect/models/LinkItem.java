package com.vineet.campusconnect.models;

public class LinkItem {
    private String title;
    private String url;
    private String description;
    private String category;
    private boolean isImportant;

    // 1. Empty constructor is REQUIRED for Firebase to work
    public LinkItem() { }

    // 2. Normal constructor to easily create new links ourselves
    public LinkItem(String title, String url, String description, String category, boolean isImportant) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.category = category;
        this.isImportant = isImportant;
    }

    // 3. Getters allow our app to read the data
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public boolean isImportant() { return isImportant; }
}