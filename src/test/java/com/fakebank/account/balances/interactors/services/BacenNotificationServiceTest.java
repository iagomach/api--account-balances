package com.fakebank.account.balances.interactors.services;

import com.fakebank.account.balances.interactors.clients.BacenClient;
import com.fakebank.account.balances.interactors.clients.bacen.models.EnumTransactionTypesModel;
import com.fakebank.account.balances.interactors.clients.bacen.models.NotificationRequestDataModel;
import com.fakebank.account.balances.interactors.services.impl.BacenNotificationServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(SpringExtension.class)
class BacenNotificationServiceTest {

    private static final String IAGO_PARI_MACHADO = "Iago Pari Machado";
    private static final String FULANO_DE_TAL = "Fulano de Tal";

    private static final EnumTransactionTypesModel expectedTransactionType =
            EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO;
    private static final BigDecimal expectedTransferAmount =
            BigDecimal.valueOf(100.00);

    private ResponseEntity<Void> bacenResponseMock;

    @Mock
    private BacenClient bacenClientMock;

    @InjectMocks
    private BacenNotificationServiceImpl bacenNotificationService;

    @BeforeEach
    void setup() {
        this.bacenNotificationService = new BacenNotificationServiceImpl(bacenClientMock);
        bacenResponseMock = new ResponseEntity<>(NO_CONTENT);
    }

    @Test
    void givenSendNotification_whenRequestBacen_thenShouldSourceNameBeFromParam() {
        //Arrange
        when(this.bacenClientMock.transferPostNotification(any())).thenReturn(bacenResponseMock);
        ArgumentCaptor<NotificationRequestDataModel> notificationRequestDataModelArgumentCaptor =
                ArgumentCaptor.forClass(NotificationRequestDataModel.class);

        //Act
        this.bacenNotificationService
                .send(IAGO_PARI_MACHADO, expectedTransferAmount, expectedTransactionType, FULANO_DE_TAL);
        //Assert
        verify(this.bacenClientMock, times(1))
                .transferPostNotification(notificationRequestDataModelArgumentCaptor.capture());
        assertEquals(IAGO_PARI_MACHADO,
                notificationRequestDataModelArgumentCaptor.getValue().getSourceAccountFullName());
    }

    @Test
    void givenSendNotification_whenRequestBacen_thenShouldTargetNameBeFromParam() {
        //Arrange
        when(this.bacenClientMock.transferPostNotification(any())).thenReturn(bacenResponseMock);
        ArgumentCaptor<NotificationRequestDataModel> notificationRequestDataModelArgumentCaptor =
                ArgumentCaptor.forClass(NotificationRequestDataModel.class);

        //Act
        this.bacenNotificationService
                .send(IAGO_PARI_MACHADO, expectedTransferAmount, expectedTransactionType, FULANO_DE_TAL);
        //Assert
        verify(this.bacenClientMock, times(1))
                .transferPostNotification(notificationRequestDataModelArgumentCaptor.capture());
        assertEquals(FULANO_DE_TAL,
                notificationRequestDataModelArgumentCaptor.getValue().getTargetAccountFullName());
    }

    @Test
    void givenSendNotification_whenRequestBacen_thenShouldTransactionTypeBeFromParam() {
        //Arrange
        when(this.bacenClientMock.transferPostNotification(any())).thenReturn(bacenResponseMock);
        ArgumentCaptor<NotificationRequestDataModel> notificationRequestDataModelArgumentCaptor =
                ArgumentCaptor.forClass(NotificationRequestDataModel.class);

        //Act
        this.bacenNotificationService
                .send(IAGO_PARI_MACHADO, expectedTransferAmount, expectedTransactionType, FULANO_DE_TAL);
        //Assert
        verify(this.bacenClientMock, times(1))
                .transferPostNotification(notificationRequestDataModelArgumentCaptor.capture());
        assertEquals(expectedTransactionType,
                notificationRequestDataModelArgumentCaptor.getValue().getTransactionType());
    }

    @Test
    void givenSendNotification_whenRequestBacen_thenShouldTransferAmountBeFromParam() {
        //Arrange
        when(this.bacenClientMock.transferPostNotification(any())).thenReturn(bacenResponseMock);
        ArgumentCaptor<NotificationRequestDataModel> notificationRequestDataModelArgumentCaptor =
                ArgumentCaptor.forClass(NotificationRequestDataModel.class);

        //Act
        this.bacenNotificationService
                .send(IAGO_PARI_MACHADO, expectedTransferAmount, expectedTransactionType, FULANO_DE_TAL);
        //Assert
        verify(this.bacenClientMock, times(1))
                .transferPostNotification(notificationRequestDataModelArgumentCaptor.capture());
        assertEquals(expectedTransferAmount,
                notificationRequestDataModelArgumentCaptor.getValue().getAmount());
    }

    @Test
    void givenSendNotification_whenBacenResponseIsError_thenShouldThrowFeignException() {
        //Arrange
        doThrow(FeignException.NotFound.class).when(this.bacenClientMock).transferPostNotification(any());
        //Act / Assert
        assertThatThrownBy(() -> this.bacenNotificationService
                .send(IAGO_PARI_MACHADO, expectedTransferAmount, expectedTransactionType, FULANO_DE_TAL))
                .isInstanceOf(FeignException.class);
    }
}
