package com.vineet.campusconnect.models;

public class Doubt {
    private String doubtId;
    private String title;
    private String description;
    private String tag;
    private String authorId;
    private String authorName;
    private long timestamp;
    private boolean isSolved;
    private int answerCount;
    private String imageUrl; // <-- NEW FIELD

    public Doubt() { }

    // Updated Constructor
    public Doubt(String title, String description, String tag, String authorId, String authorName, String imageUrl) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.authorId = authorId;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
        this.timestamp = System.currentTimeMillis();
        this.isSolved = false;
        this.answerCount = 0;
    }

    // Getters and Setters
    public String getDoubtId() { return doubtId; }
    public void setDoubtId(String doubtId) { this.doubtId = doubtId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTag() { return tag; }
    public String getAuthorName() { return authorName; }
    public String getAuthorId() { return authorId; }
    public long getTimestamp() { return timestamp; }
    public boolean isSolved() { return isSolved; }
    public int getAnswerCount() { return answerCount; }
    public String getImageUrl() { return imageUrl; } // <-- NEW GETTER
}