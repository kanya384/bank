package ru.laurkan.bank.front.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AccountResponseDTO {
    private Long id;
    private Long userId;
    private String currency;
    private Double amount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate modifiedAt;
}
