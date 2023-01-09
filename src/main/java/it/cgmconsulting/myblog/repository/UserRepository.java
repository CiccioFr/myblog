package it.cgmconsulting.myblog.repository;

import java.util.Optional;

import it.cgmconsulting.myblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // METODI DERIVATI: restituiscono SOLO entità o collection di entità, oppure primitivi/wrapper

    // uso di yaml  data.jpa.repositories.bootstrap-mode: DEFAULT | DEFERRED | LAZY
    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndEnabledTrue(long id);

    Optional<User> findByConfirmCode(String confirmCode);

    Optional<User> findByUsernameOrEmail(String username, String email);
    //Optional<User> findByUsernameOrEmailx(String username, String email);

    // Optional<User> findByUsernameAndDobBetweenAndEnabledTrue(String username, LocalDate start, LocalDate end);
    // select * from user where username='pippo' AND (dob between '2000-01-01' and '2022-12-31') and enabled=1

    // risultato della query restituisce un qualcosa che no è mai un null
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}