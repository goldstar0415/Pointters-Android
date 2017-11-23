package com.pointters.model;

/**
 * Created by prashantkumar on 22/9/17.
 */

public class UserSettingsModel {
private String title;
    private String seletedItem;

    public UserSettingsModel(String title, String seletedItem) {
        this.title = title;
        this.seletedItem = seletedItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeletedItem() {
        return seletedItem;
    }

    public void setSeletedItem(String seletedItem) {
        this.seletedItem = seletedItem;
    }
}
