package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.payload.request.ReasonRequest;
import it.cgmconsulting.myblog.service.ReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("reason")
public class ReasonController {

    @Autowired
    ReasonService reasonService;

    @PutMapping
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> saveUpdateReason(@RequestBody @Valid ReasonRequest request){
        reasonService.save(request);
        return new ResponseEntity("insert o update ok", HttpStatus.OK);
    }
}
