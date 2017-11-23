package com.pointters.model.response;

import com.pointters.model.CategoryDetails;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 28/9/17.
 */

public class GetCategoryResponse {

    private ArrayList<CategoryDetails> categories;

    public GetCategoryResponse(ArrayList<CategoryDetails> categories) {
        this.categories = categories;
    }

    public ArrayList<CategoryDetails> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryDetails> categories) {
        this.categories = categories;
    }
}
