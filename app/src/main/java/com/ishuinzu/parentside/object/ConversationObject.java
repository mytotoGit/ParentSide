package com.ishuinzu.parentside.object;

public class ConversationObject {
    private String address;
    private int count;

    public ConversationObject() {
    }

    public ConversationObject(String address, int count) {
        this.address = address;
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}