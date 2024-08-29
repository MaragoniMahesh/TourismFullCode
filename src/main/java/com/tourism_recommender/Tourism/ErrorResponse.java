package com.tourism_recommender.Tourism;

public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    // Getter and Setter
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

