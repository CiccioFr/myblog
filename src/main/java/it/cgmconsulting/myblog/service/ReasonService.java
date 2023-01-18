package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonHistory;
import it.cgmconsulting.myblog.repository.ReasonHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReasonService {
    @Autowired
    ReasonService reasonService;
    @Autowired
    ReasonHistoryRepository reasonHistoryRepository;

    public void save(Reason r) {
        reasonService.save(r);
    }

    public void save(ReasonHistory rh) {
        reasonHistoryRepository.save(rh);
    }
}
