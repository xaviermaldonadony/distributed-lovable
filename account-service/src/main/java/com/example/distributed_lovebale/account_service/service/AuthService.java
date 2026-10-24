package com.example.distributed_lovebale.account_service.service;


import com.example.distributed_lovebale.account_service.dto.auth.AuthResponse;
import com.example.distributed_lovebale.account_service.dto.auth.LoginRequest;
import com.example.distributed_lovebale.account_service.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
