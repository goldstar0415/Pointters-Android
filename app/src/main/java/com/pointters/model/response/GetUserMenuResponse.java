package com.pointters.model.response;

import com.pointters.model.BuyerModel;
import com.pointters.model.SellerModel;
import com.pointters.model.UserMenuModel;

import java.util.List;

/**
 * Created by jkc on 3/14/18.
 */


public class GetUserMenuResponse {
    private UserMenuModel counts;

    public UserMenuModel getCounts() {
        return counts;
    }

    public void setCounts(UserMenuModel counts) {
        this.counts = counts;
    }
}