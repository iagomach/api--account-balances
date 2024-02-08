package com.fakebank.account.balances.datasources.clients;

import com.fakebank.account.balances.datasources.clients.cadastro.models.DepositRequestModel;
import com.fakebank.account.balances.datasources.clients.cadastro.models.ResponseAccountIdentificationModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "cadastroClient", url = "${clients.url.cadastro}")
public interface CadastroClient {
    @GetMapping(value = "/accounts/{customerName}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseAccountIdentificationModel> getAccountsCustomerName(
            @PathVariable("customerName") String customerName);

    @PostMapping(value = "/accounts/deposit", consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> postDeposit(@RequestBody DepositRequestModel depositRequestModel);
}
