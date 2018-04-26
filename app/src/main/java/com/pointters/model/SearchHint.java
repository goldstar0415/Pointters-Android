package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchHint {

    private String title;
    private ArrayList<Object> hintsList;

    public SearchHint(String title, ArrayList<Object> hintsList) {
        this.title = title;
        this.hintsList = hintsList;
    }

    public SearchHint(String title, Object hintsList) {
        this.title = title;
        this.hintsList = (ArrayList<Object>) hintsList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Object> getHintsList() {
        return hintsList;
    }

    public void setHintsList(ArrayList<Object> hintsList) {
        this.hintsList = hintsList;
    }
}
