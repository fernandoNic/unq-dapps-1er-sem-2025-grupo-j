package unq.dapp.grupoj.soccergenius.mappers;

import org.springframework.stereotype.Component;
import unq.dapp.grupoj.soccergenius.model.dtos.RegisterFormDTO;
import unq.dapp.grupoj.soccergenius.model.dtos.UserDTO;
import unq.dapp.grupoj.soccergenius.model.User;

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
