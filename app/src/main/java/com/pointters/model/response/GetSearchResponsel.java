package com.pointters.model.response;

import com.pointters.model.PopularCateGoriyModel;
import com.pointters.model.SearchModel;

import java.util.ArrayList;

public class GetSearchResponsel {
    private ArrayList<PopularCateGoriyModel> popularCategories;
    private ArrayList<SearchModel> recentSearches;

    public ArrayList<PopularCateGoriyModel> getPopularCategories() {
        return popularCategories;
    }

    public void setPopularCategories(ArrayList<PopularCateGoriyModel> popularCategories) {
        this.popularCategories = popularCategories;
    }

    public ArrayList<SearchModel> getRecentSearches() {
        return recentSearches;
    }

    public void setRecentSearches(ArrayList<SearchModel> recentSearches) {
        this.recentSearches = recentSearches;
    }
}
