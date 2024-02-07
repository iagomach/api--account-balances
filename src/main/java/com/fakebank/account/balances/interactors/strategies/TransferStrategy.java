package com.fakebank.account.balances.interactors.strategies;

public interface TransferStrategy<R, T> {
    T transfer(R transferRequest);
}
