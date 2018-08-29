package com.pointters.model;

public class PaymentMethodResponse {
        private boolean success;
        private PaymentMethodNonceModel paymentMethod;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PaymentMethodNonceModel getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodNonceModel paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
