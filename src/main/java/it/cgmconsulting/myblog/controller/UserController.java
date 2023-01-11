package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.payload.request.UpdateUserProfile;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("user")
@Validated
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MailService mailService;

    // non messo il PreAutorized, basta il token - chiunque con un token può modificare
    @PatchMapping
    // in virtu del Tran, se il dato non varia, Hibernate non esegue una update
    @Transactional
    public ResponseEntity<?> update(@RequestBody @Valid UpdateUserProfile request, @CurrentUser UserPrincipal userPrincipal) {

        Optional<User> u = userService.findById(userPrincipal.getId());
        // verifico se esiste utente con username passata dalla request
        // se esiste, verifico che l'id dell'utente trovato NON corrisponda all'id dello UserPrincipal
        if (!u.get().getUsername().equals(request.getNewUsername()) && userService.existsByUsername(request.getNewUsername()))
            return new ResponseEntity<>("Username (or eMail) already in use", HttpStatus.FORBIDDEN);
        u.get().setUsername(request.getNewUsername());

        if (!u.get().getEmail().equals(request.getNewEmail()) && userService.existsByEmail(request.getNewEmail()))
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        u.get().setEmail(request.getNewEmail());

        return new ResponseEntity<>("User has been updated", HttpStatus.OK);
    }

    // cambio password di 2 tipi:
    //  1-  utente decide di cambiare pw
    //  2-  utente non ricorda la pw e richiede reset

    @PatchMapping("/")
    @Transactional
    public ResponseEntity<?> updatePassword(@CurrentUser UserPrincipal userPrincipal,
                @RequestParam @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "Password deve contenere tra 5 e 20 caratteri") String newPassword) {

        Optional<User> u = userService.findById(userPrincipal.getId());
        //passwordEncoder.encode(signUpRequest.getPassword().trim()),
        if (passwordEncoder.matches(newPassword, u.get().getPassword()))
            return new ResponseEntity<>("The new password is equal to old password", HttpStatus.BAD_REQUEST);
        u.get().setPassword(passwordEncoder.encode(newPassword));

        return new ResponseEntity<>("Password has been update", HttpStatus.OK);
    }

    @PostMapping("/auth")
    @Transactional
    public ResponseEntity<?> forgotPassword(@RequestParam String username){

        // genera Password troppo lunga per i controlli impostati
        //String temporaryPassword = UUID.randomUUID().toString();
        String temporaryPassword = userService.generateSecureRandomPassword();
        Optional<User> u = userService.findByUsername(username);
        if (u.isEmpty())
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        mailService.sendMail(mailService.createMail(u.get(), "Reset password request", "Please login with this temporary password: \n", temporaryPassword));
        u.get().setPassword(passwordEncoder.encode(temporaryPassword));
        return new ResponseEntity<>("Please check your eMail and follow the instructions", HttpStatus.OK);
    }
}
