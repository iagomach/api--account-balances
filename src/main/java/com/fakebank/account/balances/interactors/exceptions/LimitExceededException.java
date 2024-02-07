package com.fakebank.account.balances.interactors.exceptions;

import java.io.Serial;
import java.math.BigDecimal;

public class LimitExceededException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public LimitExceededException(BigDecimal desiredAmount, BigDecimal maxLimitAmount) {
        super("O valor da transferência: R$'"
                + desiredAmount
                + "' é maior do que o limite máximo definido: R$'"
                + maxLimitAmount
                + "'.");
    }
}
