package com.fakebank.account.balances.transportlayers;

import com.fakebank.account.balances.transportlayers.api.AccountsApi;
import com.fakebank.account.balances.transportlayers.facades.impl.TransferFacadeImpl;
import com.fakebank.account.balances.transportlayers.models.BalancesTransferRequestDataModel;
import com.fakebank.account.balances.transportlayers.models.ResponseTransferedBalancesModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController implements AccountsApi {

    private final TransferFacadeImpl transferFacade;

    public TransferController(TransferFacadeImpl transferFacade) {
        this.transferFacade = transferFacade;
    }

    @Override
    public ResponseEntity<ResponseTransferedBalancesModel>
    balancesPostTransfer(BalancesTransferRequestDataModel balancesTransferRequestDataModel) {
        return ResponseEntity.ok(this.transferFacade.transferBalances(balancesTransferRequestDataModel));
    }

}
