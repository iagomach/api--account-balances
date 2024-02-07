package com.fakebank.account.balances.transportlayers.facades;

public interface TransferFacade<R,T> {
    T transferBalances(R transferRequest);
}
