package com.pointters.model;

/**
 * Created by prashantkumar on 22/9/17.
 */

public class UserSettingsModel {

    private String title;
    private int seletedItem;

    public UserSettingsModel(String title, int seletedItem) {
        this.title = title;
        this.seletedItem = seletedItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeletedItem() {
        return seletedItem;
    }

    public void setSeletedItem(int seletedItem) {
        this.seletedItem = seletedItem;
    }
}
