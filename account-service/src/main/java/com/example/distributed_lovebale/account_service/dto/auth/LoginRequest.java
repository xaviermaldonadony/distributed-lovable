package com.example.distributed_lovebale.account_service.dto.auth;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username ,
        @Min(4) String password
) {
}
