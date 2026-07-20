package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.LoginRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.RegisterRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.AuthResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
