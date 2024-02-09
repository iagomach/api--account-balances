package com.fakebank.account.balances.repositories.exceptions;

import java.io.Serial;

public class AccountNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String fullName) {
        super("Nome " + fullName + " não encontrado na base de cadastro de clientes.");
    }
}
