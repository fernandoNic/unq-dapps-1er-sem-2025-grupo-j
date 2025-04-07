package unq.dapp._5._ercuatri.grupoj.SoccerGenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unq.dapp._5._ercuatri.grupoj.SoccerGenius.model.User;

public interface UsersRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

}