package com.pointters.model;

import com.braintreepayments.api.PaymentMethod;
import com.braintreepayments.api.models.PaymentMethodNonce;

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
