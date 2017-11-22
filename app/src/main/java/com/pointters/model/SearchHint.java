package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchHint {

    private String title;
    private ArrayList<String> hintsList;

    public SearchHint(String title, ArrayList<String> hintsList) {
        this.title = title;
        this.hintsList = hintsList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getHintsList() {
        return hintsList;
    }

    public void setHintsList(ArrayList<String> hintsList) {
        this.hintsList = hintsList;
    }
}
