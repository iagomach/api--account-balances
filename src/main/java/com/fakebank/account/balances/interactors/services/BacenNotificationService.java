package com.fakebank.account.balances.interactors.services;

import com.fakebank.account.balances.interactors.clients.bacen.models.NotificationRequestDataModel;

public interface BacenNotificationService {
    void send(NotificationRequestDataModel notificationRequestDataModel);
}
