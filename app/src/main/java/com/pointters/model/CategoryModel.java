package com.pointters.model;

import java.io.Serializable;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class CategoryModel implements Serializable{
    private String id;
    private String name;

    public CategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
