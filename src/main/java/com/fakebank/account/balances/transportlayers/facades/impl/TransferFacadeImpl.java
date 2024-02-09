package com.fakebank.account.balances.transportlayers.facades.impl;

import com.fakebank.account.balances.interactors.strategies.models.TransferSameBankRequest;
import com.fakebank.account.balances.interactors.strategies.samebank.TransferSameBankStrategy;
import com.fakebank.account.balances.transportlayers.dtos.MetaDto;
import com.fakebank.account.balances.transportlayers.facades.TransferFacade;
import com.fakebank.account.balances.transportlayers.mappers.ResponseTransferedBalancesMapper;
import com.fakebank.account.balances.transportlayers.models.BalancesTransferRequestDataModel;
import com.fakebank.account.balances.transportlayers.models.EnumTransactionTypesModel;
import com.fakebank.account.balances.transportlayers.models.ResponseTransferedBalancesModel;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TransferFacadeImpl
        implements TransferFacade<BalancesTransferRequestDataModel, ResponseTransferedBalancesModel> {
    private final TransferSameBankStrategy transferSameBankStrategy;

    public TransferFacadeImpl(TransferSameBankStrategy transferSameBankStrategy) {
        this.transferSameBankStrategy = transferSameBankStrategy;
    }

    @Override
    public ResponseTransferedBalancesModel transferBalances(BalancesTransferRequestDataModel transferRequest) {
        validateIsSupportedTransactionType(transferRequest);
        TransferSameBankRequest transferSameBankRequest = new TransferSameBankRequest
                .Builder(SecurityContextHolder.getContext().getAuthentication().getName())
                .setAmount(transferRequest.getAmount())
                .setTargetAccountFullName(transferRequest.getTargetAccountFullName())
                .build();

        var customerAccountAvailableLimit = transferSameBankStrategy.transfer(transferSameBankRequest);
        return ResponseTransferedBalancesMapper.INSTANCE.map(customerAccountAvailableLimit, new MetaDto());
    }

    private static void validateIsSupportedTransactionType(
            BalancesTransferRequestDataModel balancesTransferRequestDataModel) {
        if (!balancesTransferRequestDataModel.getTransactionType()
                .equals(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO))
            throw new NotImplementedException("Tipo de transação ainda não suportado.");
    }
}
