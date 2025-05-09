package ru.laurkan.bank.cash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CashApp {
    public static void main(String[] args) {
        SpringApplication.run(CashApp.class, args);
    }
}
