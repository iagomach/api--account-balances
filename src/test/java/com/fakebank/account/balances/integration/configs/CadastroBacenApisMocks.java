package com.fakebank.account.balances.integration.configs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class CadastroBacenApisMocks {
    public static void setupDefaultCadastroApiMocks(WireMockServer mockService) throws IOException {
        StubMapping stubMapping = mockService
                .stubFor(WireMock.get(WireMock.urlEqualTo("/customers/v1/accounts/Iago%20Pari%20Machado"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(
                                        copyToString(
                                                CadastroBacenApisMocks.class.getClassLoader()
                                                        .getResourceAsStream("payloads/get-cadastro-response.json"),
                                                defaultCharset()))));
    }

    public static void setupInactiveCadastroApiMocks(WireMockServer mockService) throws IOException {
        StubMapping stubMapping = mockService
                .stubFor(WireMock.get(WireMock.urlEqualTo("/customers/v1/accounts/Iago%20Pari%20Machado"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(
                                        copyToString(
                                                CadastroBacenApisMocks.class.getClassLoader()
                                                        .getResourceAsStream("payloads/get-cadastro-inactive-response.json"),
                                                defaultCharset()))));
    }

    public static void setupInsufficientFundsCadastroApiMocks(WireMockServer mockService) throws IOException {
        StubMapping stubMapping = mockService
                .stubFor(WireMock.get(WireMock.urlEqualTo("/customers/v1/accounts/Iago%20Pari%20Machado"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(
                                        copyToString(
                                                CadastroBacenApisMocks.class.getClassLoader()
                                                        .getResourceAsStream("payloads/get-cadastro-insufficient-funds-response.json"),
                                                defaultCharset()))));
    }

    public static void setupUpdateLimitCadastroApiMocks(WireMockServer mockService) {
        StubMapping stubMapping = mockService
                .stubFor(WireMock.post(WireMock.urlEqualTo("/customers/v1/accounts/deposit"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.NO_CONTENT.value())));
    }

    public static void setupDefaultBacenMocks(WireMockServer mockService) {
        StubMapping stubMapping = mockService
                .stubFor(WireMock.post(WireMock.urlEqualTo("/bacen/accounts/v1/transfer-notification"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.NO_CONTENT.value())));
    }
}
