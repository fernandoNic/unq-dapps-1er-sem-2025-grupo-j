package unq.dapp.grupoj.soccergenius.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import unq.dapp.grupoj.soccergenius.exceptions.AlreadyUsedEmail;
import unq.dapp.grupoj.soccergenius.model.dtos.AuthResponse;
import unq.dapp.grupoj.soccergenius.model.dtos.LoginCredentials;
import unq.dapp.grupoj.soccergenius.model.dtos.RegisterFormDTO;
import unq.dapp.grupoj.soccergenius.model.dtos.UserDTO;
import unq.dapp.grupoj.soccergenius.services.implementation.AuthService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;

    private RegisterFormDTO validRegisterForm;
    private LoginCredentials validLoginCredentials;
    private AuthResponse authResponse;

    //SE TESTEA EL CONTROLADOR, PERO SE VE INVOLUCRADO EL GLOBALEXCEPTIONHANDLER

    @BeforeEach
    void setUp() {
        validRegisterForm = new RegisterFormDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "password123");

        UserDTO userDTO = new UserDTO("John", "Doe", "john.doe@example.com");

        authResponse = new AuthResponse(userDTO, "jwt-token-value");
    }

    @Test
    void register_wrongFormatBody_returnsBadRequest() throws Exception {
        String emptyBody = "{}";
        String invalidForm = "{'asf'}";
        String invalidFormWrongEmail = "{'firstName':'fakeName', 'lastName':'fakeName', 'email':'wrongFormat', 'password':'badpass', 'passwordConfirmation':'badpass'}";
        String invalidFormEmptyFields = "{'firstName':'', 'lastName':'', 'email':'', 'password':'', 'passwordConfirmation':''}";
        String invalidFormTooShortPass = "{'firstName':'fakeName', 'lastName':'fakeName', 'email':'email@fake.com', 'password':'123', 'passwordConfirmation':'123'}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidForm)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFormWrongEmail)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFormEmptyFields)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFormTooShortPass)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_validData_returnsSuccess() throws Exception {
        // Mock the service to return a successful response
        when(authService.register(any(RegisterFormDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterForm)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer jwt-token-value"))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void register_passwordMismatch_returnsBadRequest() throws Exception {
        RegisterFormDTO mismatchedPasswords = new RegisterFormDTO(
                "John", "Doe", "john.doe@example.com",
                "password123", "differentPassword");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mismatchedPasswords)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_emailAlreadyExists_returnsConflict() throws Exception {
        when(authService.register(any(RegisterFormDTO.class)))
                .thenThrow(new AlreadyUsedEmail("Email already in use"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterForm)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_emptyCredentials_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_validCredentials_returnsSuccess() throws Exception {
        LoginCredentials credentials = new LoginCredentials("john.doe@example.com", "password123");
        when(authService.login(any(LoginCredentials.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer jwt-token-value"))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        LoginCredentials invalidCredentials = new LoginCredentials("john.doe@example.com", "wrongpass");
        when(authService.login(any(LoginCredentials.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCredentials)))
                .andExpect(status().isBadRequest());
    }
}