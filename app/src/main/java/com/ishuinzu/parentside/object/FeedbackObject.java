package com.ishuinzu.parentside.object;

public class FeedbackObject {
    private String feedback;
    private String id;
    private double rate;
    private long time;

    public FeedbackObject() {
    }

    public FeedbackObject(String feedback, String id, double rate, long time) {
        this.feedback = feedback;
        this.id = id;
        this.rate = rate;
        this.time = time;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}