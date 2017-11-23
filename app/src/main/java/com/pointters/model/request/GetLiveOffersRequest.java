package com.pointters.model.request;

import com.pointters.model.CategoryModel;
import com.pointters.model.Media;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class GetLiveOffersRequest {
    private CategoryModel categoryModel;
    private LocationRequestModel location;
    private Media media;
    private int minPrice;
    private int maxPrice;
    private String scheduleDate;

    public GetLiveOffersRequest(CategoryModel categoryModel, LocationRequestModel location, Media media, int minPrice, int maxPrice, String scheduleDate) {
        this.categoryModel = categoryModel;
        this.location = location;
        this.media = media;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.scheduleDate = scheduleDate;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public LocationRequestModel getLocation() {
        return location;
    }

    public void setLocation(LocationRequestModel location) {
        this.location = location;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
