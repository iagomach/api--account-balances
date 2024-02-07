package com.fakebank.account.balances.repositories.exceptions;

import java.io.Serial;

public class InternalServerErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InternalServerErrorException() {
        super("Ocorreu um erro inesperado. Contate o administrador do sistema.");
    }
}
