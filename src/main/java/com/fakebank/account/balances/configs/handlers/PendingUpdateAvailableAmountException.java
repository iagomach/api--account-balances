package com.fakebank.account.balances.configs.handlers;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.UUID;

public class PendingUpdateAvailableAmountException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5725139029525612550L;

    private final BigDecimal amountToSum;
    private final String fullName;
    private final UUID transactionId;

    public PendingUpdateAvailableAmountException(String fullName, BigDecimal amountToSum, UUID transactionId) {
        this.transactionId = transactionId;
        this.amountToSum = amountToSum;
        this.fullName = fullName;
    }

    public BigDecimal getAmountToSum() {
        return amountToSum;
    }

    public String getFullName() {
        return fullName;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
