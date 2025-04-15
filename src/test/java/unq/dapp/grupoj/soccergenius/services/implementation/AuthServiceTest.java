package unq.dapp.grupoj.soccergenius.services.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import unq.dapp.grupoj.soccergenius.exceptions.AlreadyUsedEmail;
import unq.dapp.grupoj.soccergenius.mappers.Mapper;
import unq.dapp.grupoj.soccergenius.model.User;
import unq.dapp.grupoj.soccergenius.model.dtos.AuthResponse;
import unq.dapp.grupoj.soccergenius.model.dtos.LoginCredentials;
import unq.dapp.grupoj.soccergenius.model.dtos.RegisterFormDTO;
import unq.dapp.grupoj.soccergenius.model.dtos.UserDTO;
import unq.dapp.grupoj.soccergenius.repository.UsersRepository;
import unq.dapp.grupoj.soccergenius.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private Mapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterFormDTO validRegistrationForm;
    private LoginCredentials validCredentials;
    private User mockUser;
    private UserDTO mockUserDTO;
    private final String TOKEN = "sample.jwt.token";

    @BeforeEach
    void setUp() {

        // Setup test data using the correct constructors based on the DTOs
        validRegistrationForm = new RegisterFormDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );

        validCredentials = new LoginCredentials(
                "john.doe@example.com",
                "password123"
        );

        mockUser = new User(1L, "John", "Doe", "john.doe@example.com", "encodedPassword");
        mockUserDTO = new UserDTO("John", "Doe", "john.doe@example.com");
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register a new user successfully")
        void shouldRegisterNewUser() {
            // Arrange
            when(usersRepository.existsByEmail(validRegistrationForm.getEmail())).thenReturn(false);
            when(mapper.toEntity(validRegistrationForm)).thenReturn(mockUser);
            when(passwordEncoder.encode(validRegistrationForm.getPassword())).thenReturn("encodedPassword");
            when(mapper.toDTO(mockUser)).thenReturn(mockUserDTO);

            try (MockedStatic<JwtTokenProvider> jwtProvider = mockStatic(JwtTokenProvider.class)) {
                jwtProvider.when(() -> JwtTokenProvider.generateToken(mockUser.getId())).thenReturn(TOKEN);

                // Act
                AuthResponse response = authService.register(validRegistrationForm);

                // Assert
                assertNotNull(response);
                assertEquals(mockUserDTO, response.getUser());
                assertEquals(TOKEN, response.getToken());

                // Verify interactions
                verify(usersRepository).existsByEmail(validRegistrationForm.getEmail());
                verify(mapper).toEntity(validRegistrationForm);
                verify(passwordEncoder).encode(validRegistrationForm.getPassword());
                verify(usersRepository).save(mockUser);
                verify(mapper).toDTO(mockUser);

                // Verify the password was set on the user
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(usersRepository).save(userCaptor.capture());
                assertEquals("encodedPassword", userCaptor.getValue().getPassword());
            }
        }

        @Test
        @DisplayName("Should throw AlreadyUsedEmail when email exists")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            when(usersRepository.existsByEmail(validRegistrationForm.getEmail())).thenReturn(true);

            // Act & Assert
            assertThrows(AlreadyUsedEmail.class, () -> authService.register(validRegistrationForm));

            // Verify
            verify(usersRepository).existsByEmail(validRegistrationForm.getEmail());
            verify(usersRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void shouldLoginSuccessfully() {
            // Arrange
            when(usersRepository.findByEmail(validCredentials.getEmail())).thenReturn(mockUser);
            when(passwordEncoder.matches(validCredentials.getPassword(), mockUser.getPassword())).thenReturn(true);
            when(mapper.toDTO(mockUser)).thenReturn(mockUserDTO);

            try (MockedStatic<JwtTokenProvider> jwtProvider = mockStatic(JwtTokenProvider.class)) {
                jwtProvider.when(() -> JwtTokenProvider.generateToken(mockUser.getId())).thenReturn(TOKEN);

                // Act
                AuthResponse response = authService.login(validCredentials);

                // Assert
                assertNotNull(response);
                assertEquals(mockUserDTO, response.getUser());
                assertEquals(TOKEN, response.getToken());

                // Verify
                verify(usersRepository).findByEmail(validCredentials.getEmail());
                verify(passwordEncoder).matches(validCredentials.getPassword(), mockUser.getPassword());
                verify(mapper).toDTO(mockUser);
            }
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(usersRepository.findByEmail(validCredentials.getEmail())).thenReturn(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> authService.login(validCredentials));
            assertEquals("Email or password is incorrect", exception.getMessage());

            // Verify
            verify(usersRepository).findByEmail(validCredentials.getEmail());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw exception when password doesn't match")
        void shouldThrowExceptionWhenPasswordDoesntMatch() {
            // Arrange
            when(usersRepository.findByEmail(validCredentials.getEmail())).thenReturn(mockUser);
            when(passwordEncoder.matches(validCredentials.getPassword(), mockUser.getPassword())).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> authService.login(validCredentials));
            assertEquals("Email or password is incorrect", exception.getMessage());

            // Verify
            verify(usersRepository).findByEmail(validCredentials.getEmail());
            verify(passwordEncoder).matches(validCredentials.getPassword(), mockUser.getPassword());
        }
    }
};