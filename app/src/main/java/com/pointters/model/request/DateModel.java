package com.pointters.model.request;

public class DateModel {
    private String serviceScheduleDate;

    public DateModel(String serviceScheduleDate) {
        this.serviceScheduleDate = serviceScheduleDate;
    }

    public String getServiceScheduleDate() {
        return serviceScheduleDate;
    }

    public void setServiceScheduleDate(String serviceScheduleDate) {
        this.serviceScheduleDate = serviceScheduleDate;
    }
}
