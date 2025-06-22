package ru.laurkan.bank.streams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableKafkaStreams
public class StreamsApp {
    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(StreamsApp.class, args);
    }
}
