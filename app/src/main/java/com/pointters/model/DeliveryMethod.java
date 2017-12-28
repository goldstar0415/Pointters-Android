package com.pointters.model;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class DeliveryMethod {

    private String title;
    private boolean isSelected;

    public DeliveryMethod(String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
