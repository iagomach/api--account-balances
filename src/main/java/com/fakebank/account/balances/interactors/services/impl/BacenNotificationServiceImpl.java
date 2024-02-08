package com.fakebank.account.balances.interactors.services.impl;

import com.fakebank.account.balances.interactors.clients.BacenClient;
import com.fakebank.account.balances.interactors.clients.bacen.models.NotificationRequestDataModel;
import com.fakebank.account.balances.interactors.services.BacenNotificationService;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class BacenNotificationServiceImpl implements BacenNotificationService {
    private final BacenClient bacenClient;
    Logger logger = LogManager.getLogger(BacenNotificationServiceImpl.class);

    public BacenNotificationServiceImpl(BacenClient bacenClient) {
        this.bacenClient = bacenClient;
    }
    @Override
    public void send(NotificationRequestDataModel notificationRequestDataModel) {
        try {
            this.bacenClient.transferPostNotification(notificationRequestDataModel);
        }
        catch (FeignException e) {
            logger.error("Erro '{}' ao enviar a notificacao para o BACEN.", e.status());
            throw e;
        }

    }
}
