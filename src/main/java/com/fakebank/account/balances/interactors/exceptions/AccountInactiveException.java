package com.fakebank.account.balances.interactors.exceptions;

import java.io.Serial;

public class AccountInactiveException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccountInactiveException(String fullName) {
        super("A conta " + fullName + " está inativa.");
    }
}
