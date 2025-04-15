package unq.dapp.grupoj.soccergenius.services.implementation;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unq.dapp.grupoj.soccergenius.exceptions.AlreadyUsedEmail;
import unq.dapp.grupoj.soccergenius.mappers.Mapper;
import unq.dapp.grupoj.soccergenius.model.dtos.AuthResponse;
import unq.dapp.grupoj.soccergenius.model.dtos.LoginCredentials;
import unq.dapp.grupoj.soccergenius.model.dtos.RegisterFormDTO;
import unq.dapp.grupoj.soccergenius.model.User;
import unq.dapp.grupoj.soccergenius.repository.UsersRepository;
import unq.dapp.grupoj.soccergenius.security.JwtTokenProvider;

@Service
public class AuthService {
    private final UsersRepository usersRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsersRepository usersRepository, Mapper mapper, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }


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
