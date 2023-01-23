package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.*;
import it.cgmconsulting.myblog.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.service.ReasonService;
import it.cgmconsulting.myblog.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("Reporting")
public class ReportingController {

    @Autowired
    ReportingService reportingService;
    @Autowired
    CommentService commentService;
    @Autowired
    ReasonService reasonService;

    /**
     * Reporting: Segnalazione di un Post
     *
     * @param request
     * @param userPrincipal
     * @return
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_READER')")
    public ResponseEntity<?> save(@RequestBody @Valid ReportingRequest request, @CurrentUser UserPrincipal userPrincipal) {

        // recuperare il Comment (identificativo commento)
        Optional<Comment> comment = commentService.findById(request.getCommentId());
        // verificare che il commento non sia già stato segnalato (altrimenti Hibernate va in UpDate)
        Optional<Reporting> r = reportingService.findByReportingId(new ReportingId(comment.get()));
        if (r.isPresent())
            return new ResponseEntity("Il commento è già stato segnalato", HttpStatus.BAD_REQUEST);

        // verificare che l'utente segnalante non sia lo stesso autore del commento
        if (comment.get().getAuthor().getId() == userPrincipal.getId())
            return new ResponseEntity<String>("You cannot send a report of this comment", HttpStatus.FORBIDDEN);

        // reuperare la reason
        // capire toUpperCase(Locale.ROOT) suggerito dall'IDE
        String reason = request.getReason().trim().toUpperCase();
        Reason rs = reasonService.getValidReason(reason);
        if (rs == null)
            return new ResponseEntity<String>("Reason not found", HttpStatus.NOT_FOUND);

        // istanziare un Reporting e salvarlo
        Reporting reporting = new Reporting(new ReportingId(comment.get()), rs, new User(userPrincipal.getId()));
        reportingService.save(reporting);

        return new ResponseEntity<String>("Report Send", HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> getReportings() {
        // cosa deve far vedere la pagina
        // return List<ReportingResponse> : commentId, utente_segnalato (username), segnalante (username), updatedAt, status, commento, reason (motivazione)
        // ordiniamo per severity DESC ,updatedAt DESC // la Severity non la mostriamo, abbiamo la reason
        // 1. creiamo la classe di response
        List<ReportingResponse> list = reportingService.getReportings();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * Il Moderatore puà aggiornare una segnalazione andandone a modificare Status e/o reason (motivazione)
     *
     * @return
     */
    @PutMapping("/{commentId}/{newStatus}/{reason}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @Transactional
    public ResponseEntity<?> update(@PathVariable long commentId, @PathVariable String newStatus, @PathVariable String reason) {
        // Cambio Status in quest'ordine: Da OPEN a in_progress a chiuso (3 stati) -> non si può tornare indietro
        // Qualora la segnalazione venga chiusa con PERMABAN o CLOSED_WITH_BAN, l'utente va disabilitato ed il commento censurato
        Optional<Reporting> rep = reportingService.findByReportingId(new ReportingId(new Comment(commentId)));
        if (rep.isEmpty())
            return new ResponseEntity<>("Reporting not found", HttpStatus.NOT_FOUND);

        return reportingService.update(rep.get(), newStatus, reason);
    }

}
