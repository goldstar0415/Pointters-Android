package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by vishalsharma on 2/8/17.
 */

public class SearchHint {

    private String title;
    private String[] hintsList;

    public SearchHint(String title, String[] hintsList) {
        this.title = title;
        this.hintsList = hintsList;
    }

    public String getTitle() {
        return title;
    }

    public String[] getHintsList() {
        return hintsList;
    }
}
