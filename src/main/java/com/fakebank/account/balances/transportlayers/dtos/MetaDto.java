package com.fakebank.account.balances.transportlayers.dtos;


import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class MetaDto {
    private final OffsetDateTime requestDateTime;

    public MetaDto() {
        this.requestDateTime = OffsetDateTime.now();
    }

    public OffsetDateTime getRequestDateTime() {
        return this.requestDateTime;
    }
}
