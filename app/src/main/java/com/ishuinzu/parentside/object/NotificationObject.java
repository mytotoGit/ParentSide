package com.ishuinzu.parentside.object;

public class NotificationObject {
    private String body;
    private long time;
    private String title;

    public NotificationObject() {
    }

    public NotificationObject(String body, long time, String title) {
        this.body = body;
        this.time = time;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}