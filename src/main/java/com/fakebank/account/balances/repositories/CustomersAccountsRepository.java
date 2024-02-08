package com.fakebank.account.balances.repositories;

import com.fakebank.account.balances.entities.accounts.CustomerAccount;

import java.math.BigDecimal;

public interface CustomersAccountsRepository {
    CustomerAccount findByName(String fullName);

    void updateAvailableLimitByName(String fullName, BigDecimal amountToSum);
}
