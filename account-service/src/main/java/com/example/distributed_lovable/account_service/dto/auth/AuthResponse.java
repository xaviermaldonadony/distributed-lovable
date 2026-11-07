package com.example.distributed_lovable.account_service.dto.auth;

public record AuthResponse(String token, UserProfileResponse user) {

}
