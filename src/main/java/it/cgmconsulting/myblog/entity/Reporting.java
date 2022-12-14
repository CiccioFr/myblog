package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * ha la particolarità che ha una Primary Key come Foreign Key
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reporting extends CreationUpdate {

    @EmbeddedId
    private ReportingId reportingId;

    @ManyToOne
    @JoinColumn(name = "reason_id")
    private Reason reason;

    @ManyToOne
    @JoinColumn(name = "reporter")
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 18)
    private ReportingStatus staus = ReportingStatus.OPEN;

    public Reporting(ReportingId reportingId, Reason reason, User reporter) {
        this.reportingId = reportingId;
        this.reason = reason;
        this.reporter = reporter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reporting reporting = (Reporting) o;
        return Objects.equals(reportingId, reporting.reportingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportingId);
    }
}
