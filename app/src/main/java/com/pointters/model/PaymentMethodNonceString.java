package com.pointters.model;

public class PaymentMethodNonceString {
    private String paymentMethodNonce;

    public PaymentMethodNonceString(String str){
        this.paymentMethodNonce = str;
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }
}
