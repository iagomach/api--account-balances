package com.fakebank.account.balances.transaportlayers.facades;

import com.fakebank.account.balances.interactors.strategies.samebank.TransferSameBankStrategy;
import com.fakebank.account.balances.transportlayers.facades.impl.TransferFacadeImpl;
import com.fakebank.account.balances.transportlayers.models.BalancesTransferRequestDataModel;
import com.fakebank.account.balances.transportlayers.models.EnumTransactionTypesModel;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
class TransferFacadeTest {
    @Mock
    TransferSameBankStrategy transferSameBankStrategy;

    @InjectMocks
    private TransferFacadeImpl transferFacade;

    @BeforeEach
    void setup() {
        transferFacade = new TransferFacadeImpl(transferSameBankStrategy);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void givenDocTransaction_whenValidate_thenThrowsException() {
        //Arrange
        var transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setTransactionType(EnumTransactionTypesModel.DOC);
        //Act and Assert
        assertThatThrownBy(() -> transferFacade.transferBalances(transferRequest))
                .isInstanceOf(NotImplementedException.class)
                .hasMessage("Método ainda não suportado.");

    }

    @Test
    void givenTedTransaction_whenValidate_thenThrowsException() {
        //Arrange
        var transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setTransactionType(EnumTransactionTypesModel.TED);
        //Act and Assert
        assertThatThrownBy(() -> transferFacade.transferBalances(transferRequest))
                .isInstanceOf(NotImplementedException.class)
                .hasMessage("Método ainda não suportado.");

    }

    @Test
    @WithMockUser(username = "Iago Pari Machado")
    void givenSameAccountTransaction_whenValidate_thenNotThrowsException() {
        //Arrange
        var transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);

        //Act and Assert
        Assertions.assertDoesNotThrow(() -> transferFacade.transferBalances(transferRequest));
    }
}

