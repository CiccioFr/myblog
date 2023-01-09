package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.request.UpdateUserProfile;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    // non messo il PreAutorized, basta il token - chiunque con un token può modificare
    @PatchMapping
    public ResponseEntity<?> update(@RequestBody @Valid UpdateUserProfile request, @CurrentUser UserPrincipal userPrincipal) {

        Optional<User> u = userService.findById(userPrincipal.getId());
        // verifico se esiste utente con username passata dalla request
        // se esiste, verifico che l'id dell'utente trovato NON corrisponda all'id dello UserPrincipal
        u.get().setUsername(request.getNewUsername());

        return null;
    }
}
