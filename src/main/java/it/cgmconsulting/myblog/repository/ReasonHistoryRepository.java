package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonHistory;
import it.cgmconsulting.myblog.entity.ReasonHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonHistoryRepository extends JpaRepository<ReasonHistory, ReasonHistoryId> {

    @Query(value = "SELECT * " +
            "FROM Reason_history rh " +
            "WHERE rh.reason.id = :reason " +
            "ORDER BY rh.start_date DESC LIMIT 1", nativeQuery = true)
    ReasonHistory getReasonHistoryByReason(@Param("reason") String reason);

    // TODO lasciate in sospeso a fine lez del 18, da testare l'indomani

    /**
     * Elenco delle reasons in corso di validità
     * @return
     */
    @Query(value = "SELECT rh.reason_id " +
            "FROM reason_history rh " +
            "WHERE rh.end_date IS NULL ORDER BY rh.reason_id ASC", nativeQuery = true)
    List<String> getReasonHistoryByEndDateIsNull();

    List<ReasonHistory> findByEndDateNull();

    //todo il contronto tra oggetti funziona solo è stato fatto l'overrider 20-10:40
    @Query(value = "SELECT rh.reasonHistoryId.reason " +
            "FROM ReasonHistory rh " +
            "INNER JOIN Reason r ON r = rh.reasonHistoryId.reason " +
            "WHERE rh.reasonHistoryId.reason.id = :reason " +
            "AND (rh.endDate Is NULL OR (CURRENT_TIMESTAMP BETWEEN rh.reasonHistoryId.startDate AND rh.endDate))")
    Reason getValidReason(@Param("reason") String reason);

}
