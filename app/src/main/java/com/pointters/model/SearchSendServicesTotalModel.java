package com.pointters.model;

import java.util.List;

/**
 * Created by mac on 12/22/17.
 */

public class SearchSendServicesTotalModel {

    private List<SearchSendServicesModel> hits;
    private int total;
    private float max_score;


    public List<SearchSendServicesModel> getHits() {
        return hits;
    }

    public void setHits(List<SearchSendServicesModel> hits) {
        this.hits = hits;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public float getMaxScore() {
        return max_score;
    }

    public void setMaxScore(float max_score) {
        this.max_score = max_score;
    }
}
