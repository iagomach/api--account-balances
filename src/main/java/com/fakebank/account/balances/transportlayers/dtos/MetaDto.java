package com.fakebank.account.balances.transportlayers.dtos;


import java.time.ZonedDateTime;

public class MetaDto {
    private final ZonedDateTime requestDateTime;

    public MetaDto() {
        this.requestDateTime = ZonedDateTime.now();
    }

    public ZonedDateTime getRequestDateTime() {
        return this.requestDateTime;
    }
}
