package com.example.dutmaintenance;

public class Reviews {
    private float rating;
    private String reviewText;
    private long timestamp;

    public Reviews() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Reviews(float rating, String reviewText, long timestamp) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
