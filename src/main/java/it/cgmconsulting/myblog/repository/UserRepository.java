package it.cgmconsulting.myblog.repository;

import java.util.Optional;

import it.cgmconsulting.myblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // uso di yaml   data.jpa.repositories.bootstrap-mode: DEFAULT | DEFERRED | LAZY
    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
    //Optional<User> findByUsernameOrEmailx(String username, String email);

    // risultato della query restituisce un qualcosa che no è mai un null
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}