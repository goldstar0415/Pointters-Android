package com.pointters.model;

import java.io.Serializable;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class CategoryModel implements Serializable{

    private String _id;
    private String name;

    public CategoryModel(String id, String name) {
        this._id = id;
        this.name = name;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
