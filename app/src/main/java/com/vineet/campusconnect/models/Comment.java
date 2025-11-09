package com.vineet.campusconnect.models;

public class Comment {
    private String text;
    private String authorId;
    private String authorName;
    private long timestamp;
    // Optional: We could add isVerified here later for teachers

    public Comment() { } // Empty constructor for Firestore

    public Comment(String text, String authorId, String authorName) {
        this.text = text;
        this.authorId = authorId;
        this.authorName = authorName;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() { return text; }
    public String getAuthorName() { return authorName; }
    public String getAuthorId() { return authorId; }
    public long getTimestamp() { return timestamp; }
}