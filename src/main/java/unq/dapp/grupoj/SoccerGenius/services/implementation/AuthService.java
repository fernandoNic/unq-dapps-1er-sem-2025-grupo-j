package unq.dapp.grupoj.SoccerGenius.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unq.dapp.grupoj.SoccerGenius.exceptions.AlreadyUsedEmail;
import unq.dapp.grupoj.SoccerGenius.mappers.Mapper;
import unq.dapp.grupoj.SoccerGenius.model.DTO.AuthResponse;
import unq.dapp.grupoj.SoccerGenius.model.DTO.LoginCredentials;
import unq.dapp.grupoj.SoccerGenius.model.DTO.RegisterFormDTO;
import unq.dapp.grupoj.SoccerGenius.model.User;
import unq.dapp.grupoj.SoccerGenius.repository.UsersRepository;
import unq.dapp.grupoj.SoccerGenius.security.JwtTokenProvider;

@Service
public class AuthService {
    @Autowired private UsersRepository usersRepository;
    @Autowired private Mapper mapper;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AuthResponse register(RegisterFormDTO registerData) {

        if (usersRepository.existsByEmail(registerData.getEmail())) {
            throw new AlreadyUsedEmail(registerData.getEmail());
        }

        User new_user = mapper.toEntity(registerData);
        new_user.setPassword(passwordEncoder.encode(registerData.getPassword()));

        usersRepository.save(new_user);

        return new AuthResponse(mapper.toDTO(new_user), JwtTokenProvider.generateToken(new_user.getId()));
    }

    public AuthResponse login(LoginCredentials credentials){
        User user = usersRepository.findByEmail(credentials.getEmail());
        if (user == null || !passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email or password is incorrect");
        }

        return new AuthResponse(mapper.toDTO(user), JwtTokenProvider.generateToken(user.getId()));
    }
}
