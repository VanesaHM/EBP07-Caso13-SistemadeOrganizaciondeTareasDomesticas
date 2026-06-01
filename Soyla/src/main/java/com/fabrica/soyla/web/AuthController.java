package com.fabrica.soyla.web;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabrica.soyla.service.SoylaService;
import com.fabrica.soyla.web.ApiModels.AuthRequest;
import com.fabrica.soyla.web.ApiModels.AuthResponse;
import com.fabrica.soyla.web.ApiModels.ConfirmEmailResponse;
import com.fabrica.soyla.web.ApiModels.RegisterRequest;
import com.fabrica.soyla.web.ApiModels.ResendConfirmationRequest;
import com.fabrica.soyla.service.TokenBlacklistService;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final SoylaService soylaService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(SoylaService soylaService, TokenBlacklistService tokenBlacklistService) {
        this.soylaService = soylaService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @jakarta.validation.Valid RegisterRequest request) {
        return soylaService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @jakarta.validation.Valid AuthRequest request) {
        return soylaService.login(request);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            tokenBlacklistService.blacklist(authorizationHeader.substring(7));
        }
        return Map.of("message", "Sesion cerrada correctamente.");
    }

    @GetMapping("/confirm")
    public ConfirmEmailResponse confirmEmail(@RequestParam String token) {
        return soylaService.confirmEmail(token);
    }

    @PostMapping("/resend-confirmation")
    public AuthResponse resendConfirmation(@RequestBody @jakarta.validation.Valid ResendConfirmationRequest request) {
        return soylaService.resendConfirmation(request);
    }
}
