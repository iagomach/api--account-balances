package com.fakebank.account.balances.transaportlayers.facades;

import com.fakebank.account.balances.entities.accounts.CustomerAccount;
import com.fakebank.account.balances.interactors.strategies.models.TransferSameBankRequest;
import com.fakebank.account.balances.interactors.strategies.samebank.TransferSameBankStrategy;
import com.fakebank.account.balances.transportlayers.facades.impl.TransferFacadeImpl;
import com.fakebank.account.balances.transportlayers.models.BalancesTransferRequestDataModel;
import com.fakebank.account.balances.transportlayers.models.EnumTransactionTypesModel;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
class TransferFacadeTest {
    public static final String TIPO_NAO_SUPORTADO = "Tipo de transação ainda não suportado.";
    @Mock
    TransferSameBankStrategy transferSameBankStrategy;

    @InjectMocks
    private TransferFacadeImpl transferFacade;

    private TransferSameBankRequest transferSameBankRequest;
    private BalancesTransferRequestDataModel balancesTransferRequestDataModel;

    @BeforeEach
    void setup() {
        transferFacade = new TransferFacadeImpl(transferSameBankStrategy);
        balancesTransferRequestDataModel = new BalancesTransferRequestDataModel();
        balancesTransferRequestDataModel.setAmount(BigDecimal.valueOf(100.00));
        balancesTransferRequestDataModel.setTargetAccountFullName("Fulano de Tal");
        balancesTransferRequestDataModel.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        transferSameBankRequest = new TransferSameBankRequest
                .Builder("Iago Pari Machado")
                .setAmount(balancesTransferRequestDataModel.getAmount())
                .setTargetAccountFullName(balancesTransferRequestDataModel.getTargetAccountFullName())
                .build();
    }

    @Test
    void givenDocTransaction_whenValidate_thenThrowsException() {
        //Arrange
        var transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setTransactionType(EnumTransactionTypesModel.DOC);
        //Act and Assert
        assertThatThrownBy(() -> transferFacade.transferBalances(transferRequest))
                .isInstanceOf(NotImplementedException.class)
                .hasMessage(TIPO_NAO_SUPORTADO);

    }

    @Test
    void givenTedTransaction_whenValidate_thenThrowsException() {
        //Arrange
        var transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setTransactionType(EnumTransactionTypesModel.TED);
        //Act and Assert
        assertThatThrownBy(() -> transferFacade.transferBalances(transferRequest))
                .isInstanceOf(NotImplementedException.class)
                .hasMessage(TIPO_NAO_SUPORTADO);

    }

    @Test
    @WithMockUser(username = "Iago Pari Machado")
    void givenSameAccountTransaction_whenValidate_thenNotThrowsException() {
        //Act and Assert
        Assertions.assertDoesNotThrow(() -> transferFacade.transferBalances(balancesTransferRequestDataModel));
    }

    @Test
    @WithMockUser(username = "Iago Pari Machado")
    void givenSameAccountTransfer_whenAuthenticated_thenGetAuthenticatedUsername() {
        //Arrange
        ArgumentCaptor<TransferSameBankRequest> transferSameBankRequestArgumentCaptor =
                ArgumentCaptor.forClass(TransferSameBankRequest.class);
        //Act
        transferFacade.transferBalances(balancesTransferRequestDataModel);

        //Assert
        verify(this.transferSameBankStrategy, times(1))
                .transfer(transferSameBankRequestArgumentCaptor.capture());
        assertEquals(transferSameBankRequest.getSourceAccountFullName(),
                transferSameBankRequestArgumentCaptor.getValue().getSourceAccountFullName());
    }

    @Test
    @WithMockUser(username = "Iago Pari Machado")
    void givenSameAccountTransfer_whenExecuteTransfer_thenSendRequestObject() {
        //Arrange
        ArgumentCaptor<TransferSameBankRequest> transferSameBankRequestArgumentCaptor =
                ArgumentCaptor.forClass(TransferSameBankRequest.class);
        //Act
        transferFacade.transferBalances(balancesTransferRequestDataModel);

        //Assert
        verify(this.transferSameBankStrategy, times(1))
                .transfer(transferSameBankRequestArgumentCaptor.capture());
        assertEquals(transferSameBankRequest.getAmount(),
                transferSameBankRequestArgumentCaptor.getValue().getAmount());
        assertEquals(transferSameBankRequest.getTargetAccountFullName(),
                transferSameBankRequestArgumentCaptor.getValue().getTargetAccountFullName());
    }

    @Test
    @WithMockUser(username = "Iago Pari Machado")
    void givenSameAccountTransferAndExecuteTransfer_whenDone_thenReturnUpdatedAvailableLimitFromResponse() {
        //Arrange
        var expectedAvailableLimit = new CustomerAccount(transferSameBankRequest.getSourceAccountFullName());
        expectedAvailableLimit.setAvailableBalancesAmountLimit(BigDecimal.valueOf(10.00));
        when(transferSameBankStrategy.transfer(any())).thenReturn(expectedAvailableLimit);

        //Act
        var result = transferFacade.transferBalances(balancesTransferRequestDataModel);

        //Assert
        assertEquals(expectedAvailableLimit.getAvailableBalancesAmountLimit(), result.getData().getAvailableAmount());
    }
}

