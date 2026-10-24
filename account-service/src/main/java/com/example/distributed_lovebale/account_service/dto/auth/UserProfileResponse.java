package com.example.distributed_lovebale.account_service.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}
