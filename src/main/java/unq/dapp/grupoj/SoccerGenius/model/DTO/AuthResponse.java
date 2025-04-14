package unq.dapp.grupoj.SoccerGenius.model.DTO;

public class AuthResponse {
    private final UserDTO user;
    private final String token;

    public AuthResponse(UserDTO user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}