package com.pointters.model;

/**
 * Created by jkc on 3/20/18.
 */

public class CardModel {
    private String cardName;
    private String cardDetail;
    private int imageRes;
    private boolean isDefault;

    public CardModel(String t, String st, int res, boolean b) {
        this.cardName = t;
        this.cardDetail = st;
        this.imageRes = res;
        this.isDefault = b;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardDetail() {
        return cardDetail;
    }

    public void setCardDetail(String cardDetail) {
        this.cardDetail = cardDetail;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean mdefault) {
        isDefault = mdefault;
    }
}
