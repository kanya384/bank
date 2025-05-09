package ru.laurkan.bank.clients.blocker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DecisionResponse {
    @JsonProperty("isBlocked")
    private boolean isBlocked;
}
