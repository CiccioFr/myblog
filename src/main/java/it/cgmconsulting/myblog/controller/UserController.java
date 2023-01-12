package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Avatar;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.payload.request.UpdateUserProfile;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.AvatarService;
import it.cgmconsulting.myblog.service.FileService;
import it.cgmconsulting.myblog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("user")
@Validated
@Slf4j
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MailService mailService;
    @Autowired
    FileService fileService;
    @Autowired
    AvatarService avatarService;

    // recupero info dal application.yaml
    @Value("${avatar.size}")
    private long size;
    @Value("${avatar.width}")
    private int width;
    @Value("${avatar.height}")
    private int height;
    @Value("${avatar.extensions}")
    private String[] extensions;

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

    /**
     * Cambio PW
     *
     * @param userPrincipal
     * @param newPassword
     * @return
     */
    @PatchMapping("/")
    @Transactional
    public ResponseEntity<?> updatePassword(@CurrentUser UserPrincipal userPrincipal,
                                            @RequestParam @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "Password deve contenere tra 5 e 20 caratteri") String newPassword) {

        Optional<User> u = userService.findById(userPrincipal.getId());
        //passwordEncoder.encode(signUpRequest.getPassword().trim()),
        // verifico che la nuova password non sia identica a quella presente sul DB
        if (passwordEncoder.matches(newPassword, u.get().getPassword()))
            return new ResponseEntity<>("The new password is equal to old password", HttpStatus.BAD_REQUEST);
        u.get().setPassword(passwordEncoder.encode(newPassword));

        return new ResponseEntity<>("Password has been update", HttpStatus.OK);
    }

    /**
     * metodo per il recupero della PW dimenticata
     *
     * @param username
     * @return
     */
    @PostMapping("/auth")
    @Transactional
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {

        // genera Password troppo lunga per i controlli impostati
        //String temporaryPassword = UUID.randomUUID().toString();
        String temporaryPassword = userService.generateSecureRandomPassword();
        Optional<User> u = userService.findByUsernameAndEnabledTrue(username);
        if (u.isEmpty())
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        mailService.sendMail(mailService.createMail(u.get(), "Reset password request", "Please login with this temporary password: \n", temporaryPassword));
        u.get().setPassword(passwordEncoder.encode(temporaryPassword));
        return new ResponseEntity<>("Please check your eMail and follow the instructions", HttpStatus.OK);
    }

    // agg avatar
    // impostiamo i vincoli sull'immagine nell yaml
    // di default si aspetta un JSON ma il multipartFile non è, e devo indicare cosa dovrà consumare
    @PatchMapping(value = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // hanno anche attributo pruduce, per l'output,
    // se vogliamo qualcosa di diverso da.., es un XML, dobbiamo indicarlo
    @Transactional
    public ResponseEntity<?> updateAvatar(@CurrentUser UserPrincipal userPrincipal,
                                          @RequestParam @NotNull MultipartFile file) throws IOException {
        if (!fileService.checkSize(file, size))
            return new ResponseEntity<>("File empty or size great then " + size, HttpStatus.BAD_REQUEST);

        if (!fileService.checkDimension(fileService.fromMultipartFileToBufferedImage(file), width, height))
            return new ResponseEntity<>("Wrong width or height image", HttpStatus.BAD_REQUEST);

        // per le estensioni, ci passa lui il metodo lungo e rognoso, affinato nei vari corsi
        if (!fileService.checkExtension(file, extensions))
            return new ResponseEntity<>("File type not allowed", HttpStatus.BAD_REQUEST);

        Optional<User> u = userService.findById(userPrincipal.getId());
        //prima di settare, devo salvare l'immagine, obbligato inqualnto l'immagine è nuova
        Avatar avatar = avatarService.fromMultipartFileToAvatar(file);

        if (u.get().getAvatar() != null)
            avatar.setId(u.get().getAvatar().getId());
        // farà l'insert se è la prima immagine che viene associata,
        // eseguirà update in DB se trova già una corrispondenza (confutata dall'if)
        avatarService.save(avatar);

        u.get().setAvatar(avatarService.fromMultipartFileToAvatar(file));

        return new ResponseEntity("Your avatar has been upsate", HttpStatus.OK);
    }

    @GetMapping("me")
    public ResponseEntity<?> getMe(@CurrentUser UserPrincipal userPrincipal) {
        Optional<User> u = userService.findById(userPrincipal.getId());
        log.info(u.get().toString());
        return new ResponseEntity(u.get(), HttpStatus.OK);
    }
}
