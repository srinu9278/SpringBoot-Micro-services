package com.hotel.payment_service.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    BANK_TRANSFER("Bank Transfer"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    RAZORPAY("Razorpay"),
    CASH("Cash"),
    WALLET("Digital Wallet"),
    UPI("UPI"),
    NET_BANKING("Net Banking");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}

