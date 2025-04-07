package unq.dapp._5._ercuatri.grupoj.SoccerGenius.mappers;

import org.springframework.stereotype.Component;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO.RegisterFormDTO;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.DTO.UserDTO;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.User;

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
        UserDTO userDTO = new UserDTO();
        userDTO.firstName = user.getFirstName();
        userDTO.lastName = user.getLastName();
        userDTO.email = user.getEmail();
        return userDTO;
    }
}
