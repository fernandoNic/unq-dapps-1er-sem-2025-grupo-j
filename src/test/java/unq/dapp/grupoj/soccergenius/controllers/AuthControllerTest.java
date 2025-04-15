package unq.dapp.grupoj.soccergenius.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import unq.dapp.grupoj.soccergenius.exceptions.AlreadyUsedEmail;
import unq.dapp.grupoj.soccergenius.exceptions.TokenVerificationException;
import unq.dapp.grupoj.soccergenius.model.dtos.AuthResponse;
import unq.dapp.grupoj.soccergenius.model.dtos.LoginCredentials;
import unq.dapp.grupoj.soccergenius.model.dtos.RegisterFormDTO;
import unq.dapp.grupoj.soccergenius.model.dtos.UserDTO;
import unq.dapp.grupoj.soccergenius.services.implementation.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private RegisterFormDTO validRegisterForm;
    private LoginCredentials validLoginCredentials;
    private AuthResponse authResponse;
    private UserDTO userDTO;
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TEST_TOKEN = "test-jwt-token";

    @BeforeEach
    void setUp() {
        // Setup valid registration form
        validRegisterForm = new RegisterFormDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        // Setup valid login credentials
        validLoginCredentials = new LoginCredentials(
                "john.doe@example.com",
                "password123"
        );

        // Setup user DTO for response
        userDTO = new UserDTO(
                "John",
                "Doe",
                "john.doe@example.com"
        );

        // Setup auth response with token
        authResponse = new AuthResponse(userDTO, TEST_TOKEN);
    }


    @Nested
    @DisplayName("Register Tests")
    class RegistrationTests {
        @Test
        @DisplayName("POST /auth/register with invalid JSON format should return 400 Bad Request")

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

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("POST /auth/register with valid data should return 200 OK with user and token")
        void register_validData_returnsSuccess() throws Exception {
            when(authService.register(any(RegisterFormDTO.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRegisterForm)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(AUTH_HEADER, TOKEN_PREFIX + TEST_TOKEN))
                    .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(userDTO.getLastName()))
                    .andExpect(jsonPath("$.email").value(userDTO.getEmail()));


            verify(authService, times(1)).register(any(RegisterFormDTO.class));
        }

        @Test
        @DisplayName("POST /auth/register with already used email should return 409 Conflict")
        void register_emailAlreadyExists_returnsConflict() throws Exception {
            when(authService.register(any(RegisterFormDTO.class)))
                    .thenThrow(new AlreadyUsedEmail(validRegisterForm.getEmail()));

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRegisterForm)))
                    .andExpect(status().isConflict())
                    .andExpect(content().string("Email already in use"));

            verify(authService, times(1)).register(any(RegisterFormDTO.class));
        }

        @Test
        @DisplayName("POST /auth/register with missing required fields should return 400 Bad Request")
        void register_missingRequiredFields_returnsBadRequest() throws Exception {
            RegisterFormDTO incompleteForm = new RegisterFormDTO(
                    "",
                    "",
                    "john.doe@example.com",
                    "password123"
            );

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(incompleteForm)))
                .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {
        @Test
        @DisplayName("POST /auth/login with empty credentials should return 400 Bad Request")
        void login_emptyCredentials_returnsBadRequest() throws Exception {
            LoginCredentials emptyCredentials = new LoginCredentials("", "");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyCredentials)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("POST /auth/login with valid credentials should return 200 OK with user and token")
        void login_validCredentials_returnsSuccess() throws Exception {
            when(authService.login(any(LoginCredentials.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validLoginCredentials)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(AUTH_HEADER, TOKEN_PREFIX + TEST_TOKEN))
                    .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(userDTO.getLastName()))
                    .andExpect(jsonPath("$.email").value(userDTO.getEmail()));

            verify(authService, times(1)).login(any(LoginCredentials.class));
        }

        @Test
        @DisplayName("POST /auth/login with invalid credentials should return 401 Unauthorized")
        void login_invalidCredentials_returnsUnauthorized() throws Exception {
            when(authService.login(any(LoginCredentials.class)))
                    .thenThrow(new TokenVerificationException("Invalid credentials"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validLoginCredentials)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Invalid credentials"));

            verify(authService, times(1)).login(any(LoginCredentials.class));
        }

        @Test
        @DisplayName("POST /auth/login with invalid email format should return 400 Bad Request")
        void login_invalidEmailFormat_returnsBadRequest() throws Exception {
            LoginCredentials invalidEmailCredentials = new LoginCredentials(
                    "not-an-email",
                    "password123"
            );

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidEmailCredentials)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("POST /auth/login with short password should return 400 Bad Request")
        void login_shortPassword_returnsBadRequest() throws Exception {
            LoginCredentials shortPasswordCredentials = new LoginCredentials(
                    "valid@example.com",
                    "short"
            );

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(shortPasswordCredentials)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.password").exists());

            verifyNoInteractions(authService);
        }
    }
}