package com.pointters.model;

public class SearchModel {
    private String query;
    private LocationModel location;
    private PopularCateGoriyModel category;
    private FilterModel filter;
    private Integer size;
    private Integer from;

    public SearchModel(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public PopularCateGoriyModel getCategory() {
        return category;
    }

    public void setCategory(PopularCateGoriyModel category) {
        this.category = category;
    }

    public FilterModel getFilter() {
        return filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }
}
