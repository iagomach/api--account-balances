package com.fakebank.account.balances.interactors.clients;

import com.fakebank.account.balances.interactors.clients.bacen.models.NotificationRequestDataModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "bacenClient", url = "${clients.url.bacen}")
public interface BacenClient {
    @PostMapping(value = "/transfer-notification", consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<Void> transferPostNotification(@RequestBody NotificationRequestDataModel customerName);
}
