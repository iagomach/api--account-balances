package com.fakebank.account.balances.transportlayers.dtos;

public class ErrorDto {
    private String code;
    private String title;
    private String detail;

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public static class Builder {
        private final String code;
        private String title;
        private String detail;

        public Builder(String code) {
            this.code = code;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public ErrorDto build() {
            ErrorDto error = new ErrorDto();
            error.code = this.code;
            error.detail = this.detail;
            error.title = this.title;
            return error;
        }
    }
}
