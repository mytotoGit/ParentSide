package com.ishuinzu.parentside.object;

public class Instruction {
    private int lottie_id;
    private String title;
    private String description;

    public Instruction() {}

    public Instruction(int lottie_id, String title, String description) {
        this.lottie_id = lottie_id;
        this.title = title;
        this.description = description;
    }

    public int getImg_id() {
        return lottie_id;
    }

    public void setImg_id(int img_id) {
        this.lottie_id = img_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
