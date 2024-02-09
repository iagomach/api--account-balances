package com.fakebank.account.balances.interactors.services.impl;

import com.fakebank.account.balances.interactors.clients.BacenClient;
import com.fakebank.account.balances.interactors.clients.bacen.models.EnumTransactionTypesModel;
import com.fakebank.account.balances.interactors.clients.bacen.models.NotificationRequestDataModel;
import com.fakebank.account.balances.interactors.services.BacenNotificationService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BacenNotificationServiceImpl implements BacenNotificationService {
    public static final String NAO_FOI_POSSIVEL_NOTIFICAR_NO_MOMENTO = "Nao foi possivel enviar notificacao da " +
            "transferencia de R${} da conta {} para {} no momento. Enviando para fila de reprocessamento.";
    private final BacenClient bacenClient;
    public static final String BACEN_NOTIFICADO_SUCESSO =
            "Transferencia de {} para {} no valor de R${} notificada com sucesso.";
    Logger logger = LogManager.getLogger(BacenNotificationServiceImpl.class);

    public BacenNotificationServiceImpl(BacenClient bacenClient) {
        this.bacenClient = bacenClient;
    }

    @Override
    @RateLimiter(name = "bacen")
    @CircuitBreaker(name = "bacenCircuitBreaker", fallbackMethod = "bacenFallback")
    @Retry(name = "bacenRateLimitRetry", fallbackMethod = "bacenFallback")
    public void send(String sourceAccountName, BigDecimal transferAmount, EnumTransactionTypesModel transactionType,
                     String targetAccountName) {
        try {
            NotificationRequestDataModel notificationRequestDataModel = buildBacenNotificationRequest(sourceAccountName,
                    transferAmount,
                    transactionType,
                    targetAccountName);
            this.bacenClient.transferPostNotification(notificationRequestDataModel);
            logger.info(BACEN_NOTIFICADO_SUCESSO,
                    sourceAccountName,
                    targetAccountName,
                    transferAmount);
        } catch (FeignException e) {
            logger.error("Erro '{}' ao enviar a notificacao para o BACEN.", e.status());
            throw e;
        }
    }

    private static NotificationRequestDataModel buildBacenNotificationRequest(String sourceAccountName,
                                                                              BigDecimal transferAmount,
                                                                              EnumTransactionTypesModel transactionType,
                                                                              String targetAccountName) {
        NotificationRequestDataModel notificationRequestDataModel = new NotificationRequestDataModel();
        notificationRequestDataModel.setSourceAccountFullName(sourceAccountName);
        notificationRequestDataModel.setAmount(transferAmount);
        notificationRequestDataModel.setTransactionType(transactionType);
        notificationRequestDataModel.setTargetAccountFullName(targetAccountName);
        return notificationRequestDataModel;
    }

    public void bacenFallback(String sourceAccountName, BigDecimal transferAmount,
                              EnumTransactionTypesModel transactionType,
                              String targetAccountName, Throwable throwable) {
        //Envia para fila de reprocessamento para quando o servico for reestabelecido.
        logger.warn(NAO_FOI_POSSIVEL_NOTIFICAR_NO_MOMENTO,
                transferAmount,
                sourceAccountName,
                targetAccountName
        );
    }
}
