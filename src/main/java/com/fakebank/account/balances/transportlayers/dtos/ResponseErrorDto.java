package com.fakebank.account.balances.transportlayers.dtos;

import java.util.List;

public class ResponseErrorDto {
    private List<ErrorDto> errors;
    private MetaDto meta;

    public List<ErrorDto> getErrors() {
        return errors;
    }

    public MetaDto getMeta() {
        return meta;
    }

    public static class Builder {
        private final List<ErrorDto> errors;
        private MetaDto meta;

        public Builder(List<ErrorDto> errors) {
            this.errors = errors;
        }

        public Builder setMeta(MetaDto meta) {
            this.meta = meta;
            return this;
        }

        public ResponseErrorDto build() {
            ResponseErrorDto responseErrorDto = new ResponseErrorDto();
            responseErrorDto.errors = this.errors;
            responseErrorDto.meta = this.meta;
            return responseErrorDto;
        }
    }
}
