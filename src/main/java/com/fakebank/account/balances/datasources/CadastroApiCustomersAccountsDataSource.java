package com.fakebank.account.balances.datasources;

import com.fakebank.account.balances.datasources.clients.CadastroClient;
import com.fakebank.account.balances.datasources.mappers.CustomerAccountMapper;
import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.repositories.CustomersAccountsRepository;
import com.fakebank.account.balances.repositories.exceptions.AccountNotFoundException;
import com.fakebank.account.balances.repositories.exceptions.InternalServerErrorException;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

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
            return CustomerAccountMapper.INSTANCE.map(cadastroClientResponse.getBody());
        } catch (FeignException.NotFound e) {
            throw new AccountNotFoundException(fullName);
        } catch (FeignException | NullPointerException e) {
            logger.error(ERROR_GETTING_CUSTOMER_DATA, e.toString());
            throw new InternalServerErrorException();
        }
    }
}
