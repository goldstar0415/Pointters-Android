package com.pointters.model;

/**
 * Created by vikas on 8/11/2017.
 */

public class NotificationOption {

    private String header;
    private String subHeader;
    private int selectedItmes;



    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    public int getSelectedItmes() {
        return selectedItmes;
    }

    public void setSelectedItmes(int selectedItmes) {
        this.selectedItmes = selectedItmes;
    }
}
