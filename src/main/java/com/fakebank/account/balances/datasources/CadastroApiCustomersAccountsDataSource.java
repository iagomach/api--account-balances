package com.fakebank.account.balances.datasources;

import com.fakebank.account.balances.datasources.clients.CadastroClient;
import com.fakebank.account.balances.datasources.clients.cadastro.models.DepositRequestModel;
import com.fakebank.account.balances.datasources.mappers.CustomerAccountMapper;
import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.repositories.CustomersAccountsRepository;
import com.fakebank.account.balances.repositories.exceptions.AccountNotFoundException;
import com.fakebank.account.balances.repositories.exceptions.InternalServerErrorException;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class CadastroApiCustomersAccountsDataSource implements CustomersAccountsRepository {

    public static final String ERROR_GETTING_CUSTOMER_DATA = "Erro ao obter cadastro do cliente destino: {}.";
    private final CadastroClient cadastroClient;

    Logger logger = LogManager.getLogger(CadastroApiCustomersAccountsDataSource.class);

    public CadastroApiCustomersAccountsDataSource(CadastroClient cadastroClient) {
        this.cadastroClient = cadastroClient;
    }

    @Override
    public CustomerAccount findByName(String fullName) {
        try {
            var cadastroClientResponse = this.cadastroClient.getAccountsCustomerName(fullName);
            CustomerAccount customerAccount = CustomerAccountMapper.INSTANCE.map(cadastroClientResponse.getBody());
            customerAccount.setName(fullName);
            return customerAccount;
        } catch (FeignException.NotFound e) {
            throw new AccountNotFoundException(fullName);
        } catch (FeignException | NullPointerException e) {
            logger.error(ERROR_GETTING_CUSTOMER_DATA, e.toString());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void updateAvailableLimitByName(String fullName, BigDecimal amountToSum) {
        try {
            this.cadastroClient.postDeposit(new DepositRequestModel()
                    .name(fullName)
                    .amount(amountToSum));
        } catch (FeignException e) {
            throw new InternalServerErrorException();
        }
    }
}

