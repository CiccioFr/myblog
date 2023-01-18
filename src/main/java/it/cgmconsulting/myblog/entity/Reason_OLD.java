package it.cgmconsulting.myblog.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Esempio di storicizzazione di dato
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reason_OLD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 30)
    private String reason;

    /**
     * corrisponde al nr di gg di ban
     * se il ban è permanente, severity = 36500
     */
    private int severity;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    public Reason_OLD(String reason, int severity, LocalDate startDate) {
        this.reason = reason;
        this.severity = severity;
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reason_OLD reason = (Reason_OLD) o;
        return id == reason.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
