package ru.laurkan.bank.oauth2proxy.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
@Setter
@Getter
public class ProxyConfigurations {
    private Map<String, Client> clients;
}
