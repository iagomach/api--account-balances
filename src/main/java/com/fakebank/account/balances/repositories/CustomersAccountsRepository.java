package com.fakebank.account.balances.repositories;

import com.fakebank.account.balances.entities.accounts.CustomerAccount;

public interface CustomersAccountsRepository {
    CustomerAccount findByName(String fullName);
}
