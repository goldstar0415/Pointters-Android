package com.pointters.model.request;

public class CancelOrderRequest {
    private String reason; // not_on_time, poor_quality_of_service, other
    private String message;

    public CancelOrderRequest(String reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
