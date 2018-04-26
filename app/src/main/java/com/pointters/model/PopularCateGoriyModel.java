package com.pointters.model;

public class PopularCateGoriyModel {
    private String icon;
    private String id;
    private String name;

    public PopularCateGoriyModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
