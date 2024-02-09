package com.fakebank.account.balances.datasources;

import com.fakebank.account.balances.configs.handlers.PendingUpdateAvailableAmountException;
import com.fakebank.account.balances.datasources.clients.CadastroClient;
import com.fakebank.account.balances.datasources.clients.cadastro.models.DepositRequestModel;
import com.fakebank.account.balances.datasources.mappers.CustomerAccountMapper;
import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.entities.accounts.StatusEnum;
import com.fakebank.account.balances.repositories.CustomersAccountsRepository;
import com.fakebank.account.balances.repositories.exceptions.AccountNotFoundException;
import com.fakebank.account.balances.repositories.exceptions.InternalServerErrorException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class CadastroApiCustomersAccountsDataSource implements CustomersAccountsRepository {

    public static final String ERROR_GETTING_CUSTOMER_DATA = "Erro ao obter cadastro do cliente destino: {}.";
    private final CadastroClient cadastroClient;

    Logger logger = LogManager.getLogger(CadastroApiCustomersAccountsDataSource.class);

    public CadastroApiCustomersAccountsDataSource(CadastroClient cadastroClient) {
        this.cadastroClient = cadastroClient;
    }

    @Override
    @Retry(name = "cadastroApiFindRetry", fallbackMethod = "findByNameFallback")
    @CircuitBreaker(name = "cadastroApiCircuitBreaker", fallbackMethod = "findByNameFallback")
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
    @CircuitBreaker(name = "cadastroApiCircuitBreaker", fallbackMethod = "updateAvailableLimitFallback")
    @Retry(name = "cadastroApiFindRetry", fallbackMethod = "updateAvailableLimitFallback")
    public void updateAvailableLimitByName(String fullName, BigDecimal amountToSum) {
        try {
            this.cadastroClient.postDeposit(new DepositRequestModel()
                    .name(fullName)
                    .amount(amountToSum));
        } catch (FeignException e) {
            throw new InternalServerErrorException();
        }
    }

    private CustomerAccount findByNameFallback(String fullName, Throwable throwable) {
        //Tenta obter do cache senao lanca excecao, vou simbolizar dessa maneira
        if (!fullName.equals("Iago Pari Machado")) {
            throw new InternalServerErrorException();
        }
        CustomerAccount customerAccount = new CustomerAccount("Iago Pari Machado");
        customerAccount.setAvailableBalancesAmountLimit(BigDecimal.valueOf(1000.00));
        customerAccount.setStatus(StatusEnum.ACTIVE);
        customerAccount.setMaxSetBalancesAmountLimit(BigDecimal.valueOf(1000.00));
        return customerAccount;

    }

    private void updateAvailableLimitFallback(String fullName, BigDecimal amountToSum, Throwable throwable) {
        //Grava transacao em cache para nova tentativa de processamento apos reestabelecimento do servico
        //Gera um id para que seja possivel acompanhar o status da transacao

        throw new PendingUpdateAvailableAmountException(fullName, amountToSum, UUID.randomUUID());
    }
}

