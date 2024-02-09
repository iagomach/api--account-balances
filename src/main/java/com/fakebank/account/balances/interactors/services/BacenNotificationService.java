package com.fakebank.account.balances.interactors.services;

import com.fakebank.account.balances.interactors.clients.bacen.models.EnumTransactionTypesModel;

import java.math.BigDecimal;

public interface BacenNotificationService {
    void send(String sourceAccountName, BigDecimal transferAmount, EnumTransactionTypesModel transactionType, String targetAccountName);
}
