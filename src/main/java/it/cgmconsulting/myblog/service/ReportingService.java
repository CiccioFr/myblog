package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.repository.ReportingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {

    @Autowired
    ReportingRepository reportingRepository;

    public void save(Reporting r) {
        reportingRepository.save(r);
    }
    public void findByReportingId(Reporting r) {
        reportingRepository.save(r);
    }
}
