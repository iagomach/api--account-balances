package com.fakebank.account.balances.interactors.exceptions;

import org.apache.logging.log4j.util.Strings;

import java.io.Serial;

public class AccountInactiveException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccountInactiveException(String fullName) {
        super("Conta" + Strings.EMPTY + fullName + Strings.EMPTY + "encontra-se inativa.");
    }
}
