package com.pointters.model;

/**
 * Created by mac on 12/8/17.
 */

public class Tags {

    private String id;
    private String type;


    public Tags(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
