package ru.laurkan.bank.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificationsApp {
    public static void main(String[] args) {
        SpringApplication.run(NotificationsApp.class, args);
    }
}
