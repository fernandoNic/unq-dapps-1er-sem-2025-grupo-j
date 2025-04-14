package unq.dapp.grupoj.soccergenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unq.dapp.grupoj.soccergenius.model.User;

public interface UsersRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

}