package com.pointters.model.request;

import com.pointters.model.CategoryModel;
import com.pointters.model.FulfillmentMethod;
import com.pointters.model.Media;
import com.pointters.model.Prices;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class AddServiceRequest {
    private CategoryModel category;
    private String description;
    private FulfillmentMethod fulfillmentMethod;
    private ArrayList<LocationRequestModel> location;
    private ArrayList<Media> media;
    private ArrayList<Prices> prices;

    public AddServiceRequest(CategoryModel category, String description, FulfillmentMethod fulfillmentMethod, ArrayList<LocationRequestModel> location, ArrayList<Media> media, ArrayList<Prices> prices) {
        this.category = category;
        this.description = description;
        this.fulfillmentMethod = fulfillmentMethod;
        this.location = location;
        this.media = media;
        this.prices = prices;
    }
}
