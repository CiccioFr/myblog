package it.cgmconsulting.myblog.payload.response;

import it.cgmconsulting.myblog.entity.ReportingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportingResponse {

    private ReportingStatus status;
    private long commentId;
    private String reporter; //nome utente segnalante
    private String commentAuthor; // nome utente segnalato (autore del commento)
    private String comment; // testo del commento
    private String reason; // motivazione della segnalazione
    private LocalDateTime updateAt; // data ultima modifica della segnalazione

}
