package unq.dapp.grupoj.soccergenius.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unq.dapp.grupoj.soccergenius.model.dtos.AuthResponse;
import unq.dapp.grupoj.soccergenius.model.dtos.LoginCredentials;
import unq.dapp.grupoj.soccergenius.model.dtos.RegisterFormDTO;
import unq.dapp.grupoj.soccergenius.model.dtos.UserDTO;
import unq.dapp.grupoj.soccergenius.services.implementation.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user and return a JWT token")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterFormDTO data) {
        AuthResponse newUser = authService.register(data);
        return ResponseEntity.ok().header("Authorization", "Bearer " + newUser.getToken()).body(newUser.getUser());
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user and return a JWT token")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginCredentials credentials) {
        AuthResponse loggedUser = authService.login(credentials);
        return ResponseEntity.ok().header("Authorization", "Bearer " + loggedUser.getToken()).body(loggedUser.getUser());
    }
}
