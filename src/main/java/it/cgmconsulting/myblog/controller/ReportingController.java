package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.*;
import it.cgmconsulting.myblog.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.service.ReasonService;
import it.cgmconsulting.myblog.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;
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
     * @param request
     * @param userPrincipal
     * @return
     */
    @PutMapping
    @PreAuthorize("hasRole('ROLE_READER')")
    public ResponseEntity<?> save(@RequestBody @Valid ReportingRequest request, @CurrentUser UserPrincipal userPrincipal) {

        // verificare che il commento non sia già stato segnalato (altrimenti Hibernate va in UpDate)
        // recuperare il commento (identificativo commento)
        Optional<Comment> comment = commentService.findById(request.getCommentId());
        if (comment.isPresent())
            new ResponseEntity<>("Il commento è già stato segnalato", HttpStatus.BAD_REQUEST);
        // verificare che l'utente segnalante non sia lo stesso autore del commento
        if (comment.get().getAuthor().getId() == userPrincipal.getId())
            return new ResponseEntity<String>("You cannot send a report of this comment", HttpStatus.FORBIDDEN);
        // reuperare la reason
        // capire toUpperCase(Locale.ROOT) suggerito
        String reason = request.getReason().trim().toUpperCase();
        Reason rs = reasonService.getValidReason(reason);
        if(rs == null)
            return new ResponseEntity<String>("Reason not found", HttpStatus.NOT_FOUND);
        // istanziare un Reporting e salvarlo
        Reporting reporting = new Reporting(new ReportingId(comment.get()), rs, new User(userPrincipal.getId()));
        reportingService.save(reporting);

        return new ResponseEntity<String>("Report Send", HttpStatus.OK);
    }
}
