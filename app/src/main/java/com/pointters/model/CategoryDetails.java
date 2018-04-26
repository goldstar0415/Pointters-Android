package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 28/9/17.
 */

public class CategoryDetails {

    private String _id;
    private String userId;
    private String  name;
    private Integer __v;
    private ArrayList<SubCategories> subCategories;
    private ArrayList<String> keywords;

    public CategoryDetails(String key){
        this.name = key;
    }

    public CategoryDetails(String _id, String userId, String name, Integer __v, ArrayList<SubCategories> subCategories, ArrayList<String> keywords) {
        this._id = _id;
        this.userId = userId;
        this.name = name;
        this.__v = __v;
        this.subCategories = subCategories;
        this.keywords = keywords;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer get__v() {
        return __v;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }

    public ArrayList<SubCategories> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategories> subCategories) {
        this.subCategories = subCategories;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
