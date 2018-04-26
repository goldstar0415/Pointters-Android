package com.pointters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ParcelModel implements Serializable {
    @SerializedName("weight")
    @Expose
    private float weight;
    @SerializedName("width")
    @Expose
    private float width;
    @SerializedName("height")
    @Expose
    private float height;
    @SerializedName("length")
    @Expose
    private float length;

    public ParcelModel() {
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }
}
