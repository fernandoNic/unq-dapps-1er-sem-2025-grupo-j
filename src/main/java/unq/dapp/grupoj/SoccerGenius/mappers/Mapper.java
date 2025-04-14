package unq.dapp.grupoj.SoccerGenius.mappers;

import org.springframework.stereotype.Component;
import unq.dapp.grupoj.SoccerGenius.model.DTO.RegisterFormDTO;
import unq.dapp.grupoj.SoccerGenius.model.DTO.UserDTO;
import unq.dapp.grupoj.SoccerGenius.model.User;

@Component
public class Mapper {
    public User toEntity(RegisterFormDTO userDTO) {
        return new User(
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPassword()
        );
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(user.getFirstName(),user.getLastName(),user.getEmail());
    }
}
