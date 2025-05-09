package ru.laurkan.bank.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExchangeApp {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeApp.class, args);
    }
}
