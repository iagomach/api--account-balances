package com.fakebank.account.balances.interactors.strategies.models;

import java.math.BigDecimal;

public class TransferSameBankRequest {
    private String sourceAccountFullName;
    private String targetAccountFullName;
    private BigDecimal amount;

    public String getSourceAccountFullName() {
        return sourceAccountFullName;
    }

    public String getTargetAccountFullName() {
        return targetAccountFullName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static class Builder {
        private String sourceAccountFullName;
        private String targetAccountFullName;
        private BigDecimal amount;

        public Builder(String sourceAccountFullName) {
            this.sourceAccountFullName = sourceAccountFullName;
        }

        public Builder setSourceAccountFullName(String sourceAccount) {
            this.sourceAccountFullName = sourceAccount;
            return this;
        }

        public Builder setTargetAccountFullName(String targetAccount) {
            this.targetAccountFullName = targetAccount;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransferSameBankRequest build() {
            TransferSameBankRequest transferSameBankRequest = new TransferSameBankRequest();
            transferSameBankRequest.amount = this.amount;
            transferSameBankRequest.sourceAccountFullName = this.sourceAccountFullName;
            transferSameBankRequest.targetAccountFullName = this.targetAccountFullName;
            return transferSameBankRequest;
        }
    }
}
