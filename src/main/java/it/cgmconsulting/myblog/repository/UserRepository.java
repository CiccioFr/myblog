package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.response.UserMe;
import it.cgmconsulting.myblog.payload.response.XlsAuthorResponse;
import it.cgmconsulting.myblog.payload.response.XlsReaderResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // METODI DERIVATI: restituiscono SOLO entità o collection di entità

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

    // se volessi andare in update su un .. specifico 9/10.34
    //tutte le in insert / update, deve essere annotata con @Modifying @Transactional
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET enable = false, updated_at = CURRENT_TIMESTAMP WHERE id = :userId", nativeQuery = true)
    void disadleUser(@Param("userId") long userId);

    // recuperare la severity del ban (per auto-riabilitare l'user a login)
/*    @Query(value = "SELECT rh.severity FROM reporting rep " +
            "INNER JOIN reason_history rh ON rep.reason_id = rh.reason_id " +
            "INNER JOIN comment c ON c.id = rep.comment_id " +
            "INNER JOIN user u ON u.id = c.author " +
            "WHERE ((u.updated_at BETWEEN rh.start_date AND rh.end_date) OR (rh.end_date IS NULL)) " +
            "AND u.id = :userId", nativeQuery = true
    )
    int getSeverity(@Param("userId") long userId);*/
    @Query(value="SELECT MAX(rh.severity) " +
            "FROM reporting rep " +
            "INNER JOIN reason_history rh ON rep.reason_id = rh.reason_id " +
            "INNER JOIN comment c ON c.id = rep.comment_id " +
            "INNER JOIN user u ON u.id = c.author " +
            "WHERE ((u.updated_at BETWEEN rh.start_date AND rh.end_date) OR (rh.end_date IS NULL)) " +
            "AND rep.status IN ('PERMABAN', 'CLOSED_WITH_BAN') " +
            "AND DATEDIFF(CURRENT_TIMESTAMP, DATE_ADD(rep.updated_at, INTERVAL rh.severity DAY)) < 0 " +
            "AND u.id = :userId", nativeQuery = true)
    int getSeverity(@Param("userId") long userId);

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.XlsAuthorResponse("
            + "u.id, "
            + "u.username, "
            + "(SELECT COUNT(p.id) FROM Post p WHERE u=p.author) AS writtenPosts, "
            + "(SELECT COALESCE(ROUND(AVG(r.rate),2), 0) FROM Rating r WHERE r.ratingId.post.author.id=u.id) AS avg "
            + ") FROM User u "
            + "INNER JOIN u.authorities a ON a.authorityName='ROLE_EDITOR' "
    )
    List<XlsAuthorResponse> getXlsAuthorResponse();

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.XlsReaderResponse(" +
            "u.id, " +
            "u.username, " +
            "(SELECT COUNT(c.id) from Comment c WHERE u=c.author) AS writtenComments, " +
            "(SELECT COUNT(r) FROM Reporting r WHERE r.reportingId.comment.author.id = u.id " +
            "AND r.status " +
            "IN (it.cgmconsulting.myblog.entity.ReportingStatus.PERMABAN, it.cgmconsulting.myblog.entity.ReportingStatus.CLOSED_WITH_BAN)) AS reportingsWithBan," +
            "u.enabled" +
            ") FROM User u " +
            "INNER JOIN u.authorities a ON a.authorityName='ROLE_READER' "
    )
    List<XlsReaderResponse> getXlsReaderResponse();
}