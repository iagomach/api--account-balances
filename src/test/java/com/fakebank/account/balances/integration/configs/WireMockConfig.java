package com.fakebank.account.balances.integration.configs;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockCadastroApiClient() {
        return new WireMockServer(3000);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockBacenApiClient() {
        return new WireMockServer(8000);
    }
}
