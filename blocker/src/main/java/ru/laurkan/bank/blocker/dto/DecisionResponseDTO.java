package ru.laurkan.bank.blocker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class DecisionResponseDTO {
    @JsonProperty("isBlocked")
    private boolean isBlocked;
}
