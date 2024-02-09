package com.fakebank.account.balances.interactors.strategies.samebank;

import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.entities.accounts.StatusEnum;
import com.fakebank.account.balances.interactors.clients.bacen.models.EnumTransactionTypesModel;
import com.fakebank.account.balances.interactors.exceptions.AccountInactiveException;
import com.fakebank.account.balances.interactors.exceptions.InsufficientFundsException;
import com.fakebank.account.balances.interactors.exceptions.LimitExceededException;
import com.fakebank.account.balances.interactors.services.BacenNotificationService;
import com.fakebank.account.balances.interactors.strategies.models.TransferSameBankRequest;
import com.fakebank.account.balances.repositories.CustomersAccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TransferSameBankStrategyTest {

    public static final String IAGO_PARI_MACHADO = "Iago Pari Machado";
    public static final String FULANO_DE_TAL = "Fulano de Tal";
    @Mock
    private BacenNotificationService bacenNotificationServiceMock;
    @Mock
    private CustomersAccountsRepository customersAccountsRepositoryMock;

    @InjectMocks
    private TransferSameBankStrategy transferSameBankStrategy;

    private CustomerAccount customerAccount;

    private TransferSameBankRequest transferSameBankRequest;

    @BeforeEach
    void setup() {
        transferSameBankStrategy = new TransferSameBankStrategy(bacenNotificationServiceMock,
                customersAccountsRepositoryMock);
        buildCustomerAccount();
        buildTransferSameBankRequest();
    }

    private void buildTransferSameBankRequest() {
        transferSameBankRequest = new TransferSameBankRequest.Builder(IAGO_PARI_MACHADO)
                .setAmount(BigDecimal.valueOf(1000.00))
                .setTargetAccountFullName(FULANO_DE_TAL)
                .build();
    }

    private void buildCustomerAccount() {
        customerAccount = new CustomerAccount(IAGO_PARI_MACHADO);
        customerAccount.setStatus(StatusEnum.ACTIVE);
        customerAccount.setAvailableBalancesAmountLimit(BigDecimal.valueOf(1000.00));
        customerAccount.setMaxSetBalancesAmountLimit(BigDecimal.valueOf(1000.00));
    }

    @Test
    void givenValidatingTransfer_whenAccountStatusInactive_thenShouldThrowExceptionWithSourceAccountNameMessage() {
        //Assert
        customerAccount.setStatus(StatusEnum.INACTIVE);
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);

        //Act / Assert
        assertThatThrownBy(() -> this.transferSameBankStrategy.transfer(transferSameBankRequest))
                .isInstanceOf(AccountInactiveException.class)
                .hasMessage("A conta " + IAGO_PARI_MACHADO + " está inativa.");
    }

    @Test
    void givenValidatingTransfer_whenTransferLimitExceeded_thenShouldThrowExceptionWithAmounts() {
        //Assert
        customerAccount.setMaxSetBalancesAmountLimit(BigDecimal.valueOf(100.00));
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);

        //Act / Assert
        assertThatThrownBy(() -> this.transferSameBankStrategy.transfer(transferSameBankRequest))
                .isInstanceOf(LimitExceededException.class)
                .hasMessage("O valor da transferência: R$"
                        + transferSameBankRequest.getAmount()
                        + " é maior do que o limite máximo definido: R$"
                        + customerAccount.getMaxSetBalancesAmountLimit()
                        + ".");
    }

    @Test
    void givenValidatingTransfer_whenAccountHasNoFunds_thenShouldThrowExceptionWithAmounts() {
        //Assert
        customerAccount.setAvailableBalancesAmountLimit(BigDecimal.valueOf(0.00));
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);

        //Act / Assert
        assertThatThrownBy(() -> this.transferSameBankStrategy.transfer(transferSameBankRequest))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("O valor da transferência R$"
                        + transferSameBankRequest.getAmount()
                        + " é maior do que o limite disponível R$"
                        + customerAccount.getAvailableBalancesAmountLimit()
                        + ".");
    }

    @Test
    void givenTransferingBalances_whenDepositToTargetAccount_thenShouldSendToTargetAccountNameAndAmountFromRequest() {
        //Assert
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);

        //Act
        this.transferSameBankStrategy.transfer(transferSameBankRequest);

        // Assert
        verify(this.customersAccountsRepositoryMock, times(1))
                .updateAvailableLimitByName(transferSameBankRequest.getTargetAccountFullName(),
                        transferSameBankRequest.getAmount());
    }

    @Test
    void givenTransferingBalances_whenDeposit_thenShouldSendNotificationToBacen() {
        //Assert
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);
        doNothing().when(this.bacenNotificationServiceMock).send(any(), any(), any(), any());

        //Act
        this.transferSameBankStrategy.transfer(transferSameBankRequest);

        // Assert
        verify(this.bacenNotificationServiceMock, times(1))
                .send(any(), any(), any(), any());
    }

    @Test
    void givenTransferingBalances_whenDeposit_thenShouldSendNotificationToBacenWithDataFromTransferRequest() {
        //Assert
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);
        doNothing().when(this.bacenNotificationServiceMock).send(any(), any(), any(), any());

        //Act
        this.transferSameBankStrategy.transfer(transferSameBankRequest);

        // Assert
        verify(this.bacenNotificationServiceMock, times(1))
                .send(transferSameBankRequest.getSourceAccountFullName(),
                        transferSameBankRequest.getAmount(),
                        EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO,
                        transferSameBankRequest.getTargetAccountFullName());
    }

    @Test
    void givenTransferingBalances_whenDone_thenShouldReturnSourceCustomerAccountNewAvailableLimit() {
        //Assert
        when(this.customersAccountsRepositoryMock.findByName(any())).thenReturn(customerAccount);
        doNothing().when(this.bacenNotificationServiceMock).send(any(), any(), any(), any());
        BigDecimal expectedAvailableBalancesAmountLimit = BigDecimal.valueOf(0.00);

        //Act
        var result = this.transferSameBankStrategy.transfer(transferSameBankRequest);

        // Assert
        assertEquals(expectedAvailableBalancesAmountLimit, result.getAvailableBalancesAmountLimit());

    }


}
