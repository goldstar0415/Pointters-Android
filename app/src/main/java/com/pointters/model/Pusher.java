package com.pointters.model;

public class Pusher {
    private String action;
    private Object object;
    private String type;
    public Pusher(String action) {
        this.action = action;
    }

    public Pusher(String action, String data) {
        this.action = action;
        this.object = data;
        this.type = "string";
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
