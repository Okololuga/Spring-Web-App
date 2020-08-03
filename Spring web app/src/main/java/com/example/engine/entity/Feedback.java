package com.example.engine.entity;

public class Feedback {
    private boolean success;
    private String feedback;

    public Feedback(boolean success) {
        this.success = success;
        this.feedback = success ? "Congratulations, you're right!" : "Wrong answer! Please, try again.";
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean isSuccess() {
        return success;
    }
}
