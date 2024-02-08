package com.fakebank.account.balances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@ServletComponentScan
public class BalancesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BalancesApplication.class, args);
    }

}
