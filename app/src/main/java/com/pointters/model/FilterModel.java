package com.pointters.model;

public class FilterModel {
    private boolean featured;
    private boolean popular;
    private boolean local;
    private boolean online;
    private boolean _new;
    private boolean services;
    private boolean jobs;

    public boolean isJobs() {
        return jobs;
    }

    public void setJobs(boolean jobs) {
        this.jobs = jobs;
    }

    public boolean isServices() {
        return services;
    }

    public void setServices(boolean services) {
        this.services = services;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean is_new() {
        return _new;
    }

    public void set_new(boolean _new) {
        this._new = _new;
    }
}
