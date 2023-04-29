package com.example.dutmaintenance;

public class Reviews {
    private float rating;
    private String reviewText;
    private Float timestamp;

    public Reviews() {

    }

    public Reviews(float rating, String reviewText, Float timestamp) {
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

    public Float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Float timestamp) {
        this.timestamp = timestamp;
    }
}
