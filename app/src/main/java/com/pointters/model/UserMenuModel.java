package com.pointters.model;

import com.pointters.model.response.BuyCounts;
import com.pointters.model.response.SellCounts;

/**
 * Created by jkc on 3/14/18.
 */


public class UserMenuModel {

    private Integer followers;
    private Integer following;
    private Integer points;
    private Integer notifications;
    private BuyCounts buy;
    private SellCounts sell;

    public UserMenuModel(){
        followers = 0;
        following = 0;
        points = 0;
        notifications = 0;
        buy = new BuyCounts();
        sell = new SellCounts();
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowings() {
        return following;
    }

    public void setFollowings(Integer following) {
        this.following = following;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getNotifications() {
        return notifications;
    }

    public void setNotifications(Integer notifications) {
        this.notifications = notifications;
    }

    public BuyCounts getBuy() {
        return buy;
    }

    public void setBuy(BuyCounts buy) {
        this.buy = buy;
    }

    public SellCounts getSell() {
        return sell;
    }

    public void setSell(SellCounts sell) {
        this.sell = sell;
    }
}