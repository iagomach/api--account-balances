package com.fakebank.account.balances.entities.accounts;

import java.math.BigDecimal;

public class CustomerAccount {
    private String name;
    private StatusEnum status;
    private BigDecimal availableBalancesAmountLimit;
    private BigDecimal maxSetBalancesAmountLimit;

    public String getName() {
        return name;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public BigDecimal getAvailableBalancesAmountLimit() {
        return availableBalancesAmountLimit;
    }

    public BigDecimal getMaxSetBalancesAmountLimit() {
        return maxSetBalancesAmountLimit;
    }

    public CustomerAccount(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public void setAvailableBalancesAmountLimit(BigDecimal availableBalancesAmountLimit) {
        this.availableBalancesAmountLimit = availableBalancesAmountLimit;
    }

    public void setMaxSetBalancesAmountLimit(BigDecimal maxSetBalancesAmountLimit) {
        this.maxSetBalancesAmountLimit = maxSetBalancesAmountLimit;
    }
}
