package unq.dapp._5._ercuatri.grupoj.SoccerGenius.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO.AuthResponse;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO.LoginCredentials;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO.RegisterFormDTO;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO.UserDTO;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.services.implementation.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user and return a JWT token")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterFormDTO data) {
        AuthResponse new_user = authService.register(data);
        return ResponseEntity.ok().header("Authorization", "Bearer " + new_user.getToken()).body(new_user.getUser());
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user and return a JWT token")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginCredentials credentials) {
        AuthResponse logged_user = authService.login(credentials);
        return ResponseEntity.ok().header("Authorization", "Bearer " + logged_user.getToken()).body(logged_user.getUser());
    }
}
