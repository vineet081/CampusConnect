package com.vineet.campusconnect.models;

public class Faculty {
    private String id; // Document ID
    private String name;
    private String designation; // e.g., Assistant Professor
    private String email;
    private String cabinNumber;
    private String imageUrl; // Photo URL

    public Faculty() { } // Required for Firestore

    public Faculty(String name, String designation, String email, String cabinNumber, String imageUrl) {
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.cabinNumber = cabinNumber;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public String getEmail() { return email; }
    public String getCabinNumber() { return cabinNumber; }
    public String getImageUrl() { return imageUrl; }
}