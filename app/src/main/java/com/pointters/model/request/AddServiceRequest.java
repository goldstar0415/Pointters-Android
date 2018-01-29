package com.pointters.model.request;

import com.pointters.model.CategoryModel;
import com.pointters.model.FulfillmentMethodForCustom1;
import com.pointters.model.Media;
import com.pointters.model.Prices;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class AddServiceRequest {

    private CategoryModel category;
    private String description;
    private FulfillmentMethodForCustom1 fulfillmentMethod;
    private ArrayList<LocationRequestModel> location;
    private ArrayList<Media> media;
    private ArrayList<Prices> prices;

    public AddServiceRequest(CategoryModel category, String description, FulfillmentMethodForCustom1 fulfillmentMethod, ArrayList<Media> media, ArrayList<Prices> prices) {
        this.category = category;
        this.description = description;
        this.fulfillmentMethod = fulfillmentMethod;
        this.location = location;
        this.media = media;
        this.prices = prices;
    }

    public void setLocation(ArrayList<LocationRequestModel> location) {
        this.location = location;
    }
}
