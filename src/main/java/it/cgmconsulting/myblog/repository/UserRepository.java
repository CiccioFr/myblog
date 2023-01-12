package it.cgmconsulting.myblog.repository;

import java.util.Optional;

import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.response.UserMe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Optional<User> findByUsernameAndEnabledTrue(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // scriviamo noi la query, occorre indicare il value
    // UserMe non essendo una entità, dobbiamo istanziare all'interno della query un oggetto di tipo UserMe
    // JPQL Java Persistent Query Language
    // attributi scritti come nella classe UserMe //             "u.email AS mail" +
    // Nelle Query: scrittura posizionale ?1 oppure con variabile usando i 2 punti  :id
    // es  WHERE u.id = ?1   <-->   WHERE u.id = :id
    // TUTTE LE FOREIGNKEY RAPPRESENTANO PER HIBERNATE DEGLI OGGETTI
    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.UserMe(u.id, u.username, u.email, ava) " +
            "FROM User u " +
            "LEFT JOIN Avatar ava ON u.avatar.id = ava.id " + // uguale a " ON u.avatar = ava "
            "WHERE u.id = :id"
    )
    UserMe getMe(@Param("id") long id); //quando devo passare parametri, li annoto con l'annotation @Param
}