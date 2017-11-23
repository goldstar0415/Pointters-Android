package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 28/9/17.
 */

public class SubCategories {
    private String name;
    private String _id;
    private ArrayList<String> keywords;


    public SubCategories(String name, String _id, ArrayList<String> keywords) {
        this.name = name;
        this._id = _id;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
