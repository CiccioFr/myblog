package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.ReasonHistory;
import it.cgmconsulting.myblog.entity.ReasonHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonHistoryRepository extends JpaRepository<ReasonHistory, ReasonHistoryId> {
}
