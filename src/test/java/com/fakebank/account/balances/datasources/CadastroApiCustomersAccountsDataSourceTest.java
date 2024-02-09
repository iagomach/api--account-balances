package com.fakebank.account.balances.datasources;

import com.fakebank.account.balances.datasources.clients.CadastroClient;
import com.fakebank.account.balances.datasources.clients.cadastro.models.AccountIdentificationDataModel;
import com.fakebank.account.balances.datasources.clients.cadastro.models.BalancesTransferLimitDataModel;
import com.fakebank.account.balances.datasources.clients.cadastro.models.EnumAccountStatusModel;
import com.fakebank.account.balances.datasources.clients.cadastro.models.ResponseAccountIdentificationModel;
import com.fakebank.account.balances.repositories.exceptions.AccountNotFoundException;
import com.fakebank.account.balances.repositories.exceptions.InternalServerErrorException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CadastroApiCustomersAccountsDataSourceTest {

    @Mock
    private CadastroClient cadastroClientMock;

    @InjectMocks
    private CadastroApiCustomersAccountsDataSource cadastroApiCustomersAccountsDataSource;

    private ResponseAccountIdentificationModel responseAccountIdentificationModel;

    private static final String IAGO_PARI_MACHADO = "IAGO_PARI_MACHADO";

    @BeforeEach
    void setup() {
        setResponseAccountIdentificationModelMock();
        cadastroApiCustomersAccountsDataSource = new CadastroApiCustomersAccountsDataSource(this.cadastroClientMock);
    }

    private void setResponseAccountIdentificationModelMock() {
        AccountIdentificationDataModel accountIdentificationDataModel = new AccountIdentificationDataModel();
        BalancesTransferLimitDataModel balancesTransferLimitDataModel = new BalancesTransferLimitDataModel();
        balancesTransferLimitDataModel.setAvailableAmount(BigDecimal.valueOf(1000.00));
        balancesTransferLimitDataModel.setMaxSetAmount(BigDecimal.valueOf(1000.00));
        accountIdentificationDataModel.setStatus(EnumAccountStatusModel.ACTIVE);
        accountIdentificationDataModel.setBalancesTransferLimit(balancesTransferLimitDataModel);
        responseAccountIdentificationModel = new ResponseAccountIdentificationModel();
        responseAccountIdentificationModel.setData(accountIdentificationDataModel);
    }

    @Test
    void givenFindByName_whenRequestCadastroApi_thenShouldFindNameReceivedInParam() {
        //Arrange
        when(this.cadastroClientMock.getAccountsCustomerName(any()))
                .thenReturn(ResponseEntity.ok(responseAccountIdentificationModel));

        //Act
        cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO);

        //Assert
        Mockito.verify(cadastroClientMock, times(1)).getAccountsCustomerName(IAGO_PARI_MACHADO);

    }

    @Test
    void givenFindByName_whenRequestCadastroApiSuccess_thenShouldCustomerAccountAvailableLimitBeSameFromClient() {
        //Arrange
        when(this.cadastroClientMock.getAccountsCustomerName(any()))
                .thenReturn(ResponseEntity.ok(responseAccountIdentificationModel));
        var expectedAvailableLimit = responseAccountIdentificationModel.getData().getBalancesTransferLimit()
                .getAvailableAmount();

        //Act
        var result = cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO);

        //Assert
        assertEquals(expectedAvailableLimit, result.getAvailableBalancesAmountLimit());

    }

    @Test
    void givenFindByName_whenRequestCadastroApiSuccess_thenShouldCustomerAccountMaxSetAmountLimitBeSameFromClient() {
        //Arrange
        when(this.cadastroClientMock.getAccountsCustomerName(any()))
                .thenReturn(ResponseEntity.ok(responseAccountIdentificationModel));
        var expectedMaxSetAmount = responseAccountIdentificationModel.getData().getBalancesTransferLimit()
                .getMaxSetAmount();

        //Act
        var result = cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO);

        //Assert
        assertEquals(expectedMaxSetAmount, result.getMaxSetBalancesAmountLimit());

    }

    @Test
    void givenFindByName_whenRequestCadastroApiSuccess_thenShouldCustomerAccountStatusBeSameFromClient() {
        //Arrange
        when(this.cadastroClientMock.getAccountsCustomerName(any()))
                .thenReturn(ResponseEntity.ok(responseAccountIdentificationModel));
        var expectedStatus = responseAccountIdentificationModel.getData().getStatus();

        //Act
        var result = cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO);

        //Assert
        assertEquals(expectedStatus.name(), result.getStatus().name());

    }

    @Test
    void givenFindByName_whenRequestCadastroApiSuccess_thenShouldCustomerAccountNameBeSameFromReceivedParam() {
        //Arrange
        when(this.cadastroClientMock.getAccountsCustomerName(any()))
                .thenReturn(ResponseEntity.ok(responseAccountIdentificationModel));

        //Act
        var result = cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO);

        //Assert
        assertEquals(IAGO_PARI_MACHADO, result.getName());
    }

    @Test
    void givenFindByName_whenCadastroApiResponseIsNotFound_thenShouldThrowNotFoundExceptionWithFullNameParam() {
        //Arrange
        doThrow(FeignException.NotFound.class).when(this.cadastroClientMock).getAccountsCustomerName(any());
        var expectedErrorMessage = "Nome " + IAGO_PARI_MACHADO + " não encontrado na base de cadastro de clientes.";

        //Act / Assert
        assertThatThrownBy(() -> cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage(expectedErrorMessage);
    }

    @Test
    void givenFindByName_whenCadastroApiResponseIsAnyFeignException_thenShouldThrowInternalServerErrorException() {
        //Arrange
        doThrow(FeignException.class).when(this.cadastroClientMock).getAccountsCustomerName(any());

        //Act / Assert
        assertThatThrownBy(() -> cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO))
                .isInstanceOf(InternalServerErrorException.class);
    }

    @Test
    void givenFindByName_whenCadastroApiResponseBodyIsNull_thenShouldThrowInternalServerErrorException() {
        //Arrange
        when(this.cadastroClientMock.getAccountsCustomerName(any()))
                .thenReturn(ResponseEntity.ok(null));

        //Act / Assert
        assertThatThrownBy(() -> cadastroApiCustomersAccountsDataSource.findByName(IAGO_PARI_MACHADO))
                .isInstanceOf(InternalServerErrorException.class);
    }
}
