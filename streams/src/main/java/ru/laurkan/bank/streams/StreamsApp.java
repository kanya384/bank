package ru.laurkan.bank.streams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class StreamsApp {
    public static void main(String[] args) {
        SpringApplication.run(StreamsApp.class, args);
    }
}
