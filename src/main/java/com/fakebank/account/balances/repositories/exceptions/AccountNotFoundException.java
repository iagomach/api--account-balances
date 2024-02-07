package com.fakebank.account.balances.repositories.exceptions;

import org.apache.logging.log4j.util.Strings;

import java.io.Serial;

public class AccountNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String fullName) {
        super("Nome" + Strings.EMPTY + fullName + Strings.EMPTY + "não encontrado na base de cadastro de clientes.");
    }
}
