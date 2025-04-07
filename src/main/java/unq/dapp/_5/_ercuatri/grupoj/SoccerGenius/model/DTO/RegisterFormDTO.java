package unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO;


import jakarta.validation.constraints.*;

public class RegisterFormDTO {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotNull(message = "Password confirmation is required")
    private String passwordConfirmation;
    @AssertTrue
    private boolean isPasswordConfirmed() {
        return password != null && password.equals(passwordConfirmation);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }
}

