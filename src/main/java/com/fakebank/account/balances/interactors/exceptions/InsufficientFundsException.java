package com.fakebank.account.balances.interactors.exceptions;

import java.io.Serial;
import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(BigDecimal desiredAmount, BigDecimal availableAmount) {
        super("O valor da transferência R$"
                + String.format("%.2f", desiredAmount)
                + " é maior do que o limite disponível R$"
                + String.format("%.2f", availableAmount)
                + ".");
    }
}
