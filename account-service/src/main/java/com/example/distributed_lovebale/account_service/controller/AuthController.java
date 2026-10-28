package com.example.distributed_lovebale.account_service.controller;

import com.example.distributed_lovebale.account_service.dto.auth.AuthResponse;
import com.example.distributed_lovebale.account_service.dto.auth.LoginRequest;
import com.example.distributed_lovebale.account_service.dto.auth.SignupRequest;
import com.example.distributed_lovebale.account_service.dto.auth.UserProfileResponse;
import com.example.distributed_lovebale.account_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
//    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup (@RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@RequestBody LoginRequest request){
       return ResponseEntity.ok(authService.login(request));
    }

//    @GetMapping("/me")
//    public ResponseEntity<UserProfileResponse> getProfile() {
//        Long userId = 1L;
//        return ResponseEntity.ok(userService.getProfile(userId));
//    }
    // TO DO
}
