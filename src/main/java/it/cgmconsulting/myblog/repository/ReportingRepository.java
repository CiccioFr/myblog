package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.entity.ReportingId;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// passiamo a JpaRepository<..> l'entità ed il tipo della chiave
@Repository
public interface ReportingRepository extends JpaRepository<Reporting, ReportingId> {

    // metodo derivato
    Optional<Reporting> findByReportingId(ReportingId rId);

    // todo manca un OR nel WHERE
    @Query(value="SELECT new it.cgmconsulting.myblog.payload.response.ReportingResponse(" +
            "r.status, " +
            "r.reportingId.comment.id, " +
            "r.reporter.username, " +
            "r.reportingId.comment.author.username, " +
            "r.reportingId.comment.comment, " +
            "rh.reasonHistoryId.reason.id, " +
            "r.createdAt) " +
            "FRoM Reporting r " +
            "INNER JOIN ReasonHistory rh ON rh.reasonHistoryId.reason.id = r.reason.id " +
            "WHERE ((rh.endDate IS NULL AND rh.reasonHistoryId.startDate <= CURRENT_TIMESTAMP) " +
            "   OR (r.createdAt BETWEEN rh.reasonHistoryId.startDate AND rh.endDate)) " +
            "ORDER BY rh.severity DESC, r.updatedAt DESC")
    List<ReportingResponse> getReportings();
}