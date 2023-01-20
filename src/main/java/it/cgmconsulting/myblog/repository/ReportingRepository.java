package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.entity.ReportingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// passiamo l'entità, ed il tipo della chiave
@Repository
public interface ReportingRepository extends JpaRepository<Reporting, ReportingId> {
}
