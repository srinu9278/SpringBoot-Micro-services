package com.hotel.payment_service.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    PAYMENT("Payment"),
    REFUND("Refund"),
    PARTIAL_REFUND("Partial Refund"),
    CHARGEBACK("Chargeback"),
    DISPUTE("Dispute"),
    ADJUSTMENT("Adjustment"),
    COMMISSION("Commission"),
    FEE("Fee");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }
}

