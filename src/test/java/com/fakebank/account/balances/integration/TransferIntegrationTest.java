package com.fakebank.account.balances.integration;

import com.fakebank.account.balances.integration.configs.WireMockConfig;
import com.fakebank.account.balances.transportlayers.models.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.fakebank.account.balances.configs.handlers.RestResponseEntityExceptionHandler.*;
import static com.fakebank.account.balances.integration.configs.CadastroBacenApisMocks.*;
import static com.fakebank.account.balances.transportlayers.models.EnumErrorsBalancesTransferModel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableFeignClients
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ServletComponentScan
@ContextConfiguration(classes = {WireMockConfig.class})
public class TransferIntegrationTest {
    public static final String TRANSFER_ENDPOINT = "/accounts/v1/transfer";
    public static final String IAGO_PARI_MACHADO = "Iago Pari Machado";
    public static final String ITAU = "itau";
    public static final String FULANO_DE_TAL = "Fulano de Tal";
    @Autowired
    private WireMockServer mockCadastroApiClient;

    @Autowired
    private WireMockServer mockBacenApiClient;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders requestHeaders;

    @BeforeEach
    void setUp() {
        requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void givenTransferAmountZero_whenSendRequest_thenShouldReturnBadRequest() {

        //Arrange
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(0.00));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);

        //Act

        ResponseEntity<String> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, String.class);

        //Assert
        assertEquals(BAD_REQUEST, response.getStatusCode());

    }

    @Test
    void givenTransferAmountLessThanZero_whenSendRequest_thenShouldReturnBadRequest() {

        //Arrange
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(-1.00));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);

        //Act

        ResponseEntity<String> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, String.class);

        //Assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void givenTransferNullTargetAccount_whenSendRequest_thenShouldReturnBadRequest() {

        //Arrange
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(10.00));
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);

        //Act

        ResponseEntity<String> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, String.class);

        //Assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void givenTransferNullTransactionType_whenSendRequest_thenShouldReturnBadRequest() {

        //Arrange
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(10.00));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);

        //Act

        ResponseEntity<String> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, String.class);

        //Assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void givenTransferRequest_whenLimitExceeded_thenShouldReturnUnprocessableEntityWithMessage() throws IOException {

        //Arrange
        setupDefaultCadastroApiMocks(mockCadastroApiClient);
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(3000.00));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);


        var errorResponseEntity = new ResponseErrorMetaSingleErrorsInnerModel();
        errorResponseEntity.setCode(VALOR_ACIMA_LIMITE.name());
        errorResponseEntity.setTitle(VALOR_ACIMA_DO_LIMITE_TITLE);
        errorResponseEntity.setDetail("O valor da transferência: R$"
                + String.format("%.2f", transferRequest.getAmount())
                + " é maior do que o limite máximo definido: R$1000,00.");

        var expectedResponse = List.of(errorResponseEntity);

        //Act
        ResponseEntity<ResponseErrorMetaSingleModel> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, ResponseErrorMetaSingleModel.class);

        //Assert
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(expectedResponse, Objects.requireNonNull(response.getBody()).getErrors());
    }

    @Test
    void givenTransferRequest_whenSourceAccountInactive_thenShouldReturnUnprocessableEntityWithMessage() throws IOException {

        //Arrange
        setupInactiveCadastroApiMocks(mockCadastroApiClient);
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(100.00));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);


        var errorResponseEntity = new ResponseErrorMetaSingleErrorsInnerModel();
        errorResponseEntity.setCode(CONTA_INATIVA.name());
        errorResponseEntity.setTitle(CONTA_ORIGEM_INATIVA);
        errorResponseEntity.setDetail("A conta " + IAGO_PARI_MACHADO + " está inativa.");

        var expectedResponse = List.of(errorResponseEntity);

        //Act
        ResponseEntity<ResponseErrorMetaSingleModel> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, ResponseErrorMetaSingleModel.class);

        //Assert
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(expectedResponse, Objects.requireNonNull(response.getBody()).getErrors());
    }

    @Test
    void givenTransferRequest_whenSourceAccountNoFunds_thenShouldReturnUnprocessableEntityWithMessage() throws IOException {

        //Arrange
        setupInsufficientFundsCadastroApiMocks(mockCadastroApiClient);
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(1000.00));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);


        var errorResponseEntity = new ResponseErrorMetaSingleErrorsInnerModel();
        errorResponseEntity.setCode(SALDO_INSUFICIENTE.name());
        errorResponseEntity.setTitle(SALDO_INSUFICIENTE_TITLE);
        errorResponseEntity.setDetail("O valor da transferência R$"
                + String.format("%.2f", transferRequest.getAmount())
                + " é maior do que o limite disponível R$100,00.");

        var expectedResponse = List.of(errorResponseEntity);

        //Act
        ResponseEntity<ResponseErrorMetaSingleModel> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, ResponseErrorMetaSingleModel.class);

        //Assert
        assertEquals(UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(expectedResponse, Objects.requireNonNull(response.getBody()).getErrors());
    }

    @Test
    void givenTransferRequest_whenSuccessDone_thenShouldReturnUpdatedAmount() throws IOException {

        //Arrange
        setupDefaultCadastroApiMocks(mockCadastroApiClient);
        setupDefaultBacenMocks(mockBacenApiClient);
        setupUpdateLimitCadastroApiMocks(mockCadastroApiClient);
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(999.98));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);
        var balancesTransferDataModel = new BalancesTransferDataModel();
        balancesTransferDataModel.setAvailableAmount(BigDecimal.valueOf(0.02));
        var expectedResponse = new ResponseTransferedBalancesModel();
        expectedResponse.setData(balancesTransferDataModel);


        //Act
        ResponseEntity<ResponseTransferedBalancesModel> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, ResponseTransferedBalancesModel.class);

        //Assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedResponse.getData(), Objects.requireNonNull(response.getBody()).getData());
    }

    @Test
    void givenTransferRequest_whenBacenApiHasUnavailability_thenShouldContinueWithFallback() throws IOException {

        //Arrange
        setupDefaultCadastroApiMocks(mockCadastroApiClient);
        setupUpdateLimitCadastroApiMocks(mockCadastroApiClient);
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(999.98));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);


        //Act
        ResponseEntity<ResponseTransferedBalancesModel> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, ResponseTransferedBalancesModel.class);

        //Assert
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void givenTransferRequest_whenCadastroApiHasUnavailability_thenShouldContinueWithFallback() {

        //Arrange

        setupDefaultBacenMocks(mockBacenApiClient);
        BalancesTransferRequestDataModel transferRequest = new BalancesTransferRequestDataModel();
        transferRequest.setAmount(BigDecimal.valueOf(999.98));
        transferRequest.setTargetAccountFullName(FULANO_DE_TAL);
        transferRequest.setTransactionType(EnumTransactionTypesModel.TRANSFERENCIA_MESMA_INSTITUICAO);
        HttpEntity<BalancesTransferRequestDataModel> request = new HttpEntity<>(transferRequest, requestHeaders);

        BalancesTransferPendingDataModel balancesTransferPendingDataModel = new BalancesTransferPendingDataModel();
        balancesTransferPendingDataModel.setStatus(EnumTransferPendingStatusModel.PENDENTE_DEPOSITO);
        balancesTransferPendingDataModel.setTransactionId(UUID.randomUUID());
        ResponsePendingTransferModel expectedResponse = new ResponsePendingTransferModel();
        expectedResponse.setData(balancesTransferPendingDataModel);


        //Act
        ResponseEntity<ResponsePendingTransferModel> response = restTemplate.withBasicAuth(IAGO_PARI_MACHADO, ITAU)
                .postForEntity(TRANSFER_ENDPOINT, request, ResponsePendingTransferModel.class);

        //Assert
        assertEquals(ACCEPTED, response.getStatusCode());
        assertTrue(Pattern.matches("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})", expectedResponse.getData().getTransactionId().toString()));
        assertEquals(expectedResponse.getData().getStatus(), Objects.requireNonNull(response.getBody()).getData().getStatus());

    }

    @Test
    void givenTransferRequest_whenNotAuthenticated_thenShouldUnauthorize() {

        //Arrange
        ResponseEntity<String> response = restTemplate
                .postForEntity(TRANSFER_ENDPOINT, new BalancesTransferRequestDataModel(), String.class);

        //Assert
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

}
