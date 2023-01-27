package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Authority;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.payload.request.SigninRequest;
import it.cgmconsulting.myblog.payload.request.SignupRequest;
import it.cgmconsulting.myblog.payload.request.UpdateUserAuthority;
import it.cgmconsulting.myblog.payload.response.JwtAuthenticationResponse;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.JwtTokenProvider;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.AuthorityService;
import it.cgmconsulting.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Classe per la creazione di servizi RESTful
 */
@RestController
@RequestMapping("auth") // localhost:{port}/auth/....
@Validated
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    AuthorityService authorityService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    MailService mailService;

    /**
     * Login Utente all'url localhost:{port}/auth/signin
     * * Verifica:
     * 1. Se user Esiste
     * 2. Se user è Abilitato (da Admin)
     * 3. Se user è Bannato
     * 4. Se Ban è Scaduto (se si lo riabilita)
     * 5. permette login e Crea Token
     *
     * @param request Bean SigninRequest con usernameOrEmail e password
     * @return Response con ID UserName Mail Ruolo Token
     */
    @PostMapping("signin") // localhost:{port}/auth/signin
    @Transactional
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest request) {
        Optional<User> u = userService.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail());
        if (!u.isPresent())
            return new ResponseEntity<String>("Bad Credentials", HttpStatus.FORBIDDEN);

        // check se utente è bannato; se ban scaduto, riabilito user
        String s = null;
        if (!u.get().isEnabled()) {
            s = userService.checkBan(u.get(), u.get().getUpdatedAt());
            if (s != null)
                return new ResponseEntity<String>(s, HttpStatus.FORBIDDEN);
        }
        //
        if (!passwordEncoder.matches(request.getPassword(), u.get().getPassword()))
            return new ResponseEntity<String>("Bad Credentials", HttpStatus.FORBIDDEN);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        // è un oggetto di Spring Security
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // creazione Token
        String jwt = JwtTokenProvider.generateToken(authentication);
        // creazione DTO
        // sostanz è una mappatura tra ..
        JwtAuthenticationResponse currentUser = UserPrincipal
                .createJwtAuthenticationResponseFromUserPrincipal((UserPrincipal) authentication.getPrincipal(), jwt);

        return ResponseEntity.ok(currentUser);
    }

    /**
     * ResponseEntity oggetto di Spring che ci permette di avere al suo interno 2 valori fondamenti nella response
     * : il Body e HttpStatus
     * body la lista di cio che realmente ci aspettiamo
     * HttpStatus: il codice
     *
     * @param signUpRequest
     * @return Response Body e HttpStatus
     */
    @PutMapping("/signup")
    // permette di non avere salvataggi monchi sul DB
    @Transactional
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Username already in use", HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Email Address already in use!", HttpStatus.BAD_REQUEST);
        }

        // find default valid authority
        Optional<Authority> authority = authorityService.findByAuthorityName("ROLE_GUEST");

        // genera un codice alfanumerico univoco
        // Creating user's account // User user = new User();
        String confirmCode = UUID.randomUUID().toString();
        // Creating user's account
        User user = new User(
                signUpRequest.getUsername().trim(),
                signUpRequest.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(signUpRequest.getPassword().trim()),
                // .get() per estrarre da Optional il ruolo
                Collections.singleton(authority.get()), // transforms object Authority into Set<Authority>
                confirmCode
        );

        // con @Transactional usiamo il Metodo save quando creiamo un nuovo oggetto da persistere sul DB
        userService.save(user);
        // fatta la persistenza, invio la mail
        // invio confirm code
        mailService.sendMail(mailService.createMail(user, "Myblog - Confirm code",
                "In order to confirm your registration, please click this link http://localhost:8083/auth/confirm/"
                        + confirmCode, "  <- Click"));

        return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }

    /**
     * modifica del ruolo
     *
     * @param request       composta da userId e Set<String> authorities
     * @param userPrincipal
     * @return
     */
    //@PatchMapping("/{userId}")  // localhost:{port}/auth/4/ROLE_EDITOR,ROLE_MODERATOR // conviene creare classe di request
    //public ResponseEntity<?> updateAuthority(@PathVariable long userId){
    @PatchMapping()  // localhost:{port}/auth/4/ROLE_EDITOR,ROLE_MODERATOR
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> updateAuthority(@RequestBody @Valid UpdateUserAuthority request, @CurrentUser UserPrincipal userPrincipal) {

        // Evito che l'Admin si cambi le autorizzazioni
        // Un utente avente il ruolo di ADMIN  non può modificare i ruoli su se stesso.
        if (request.getUserId() == userPrincipal.getId())
            return new ResponseEntity<String>("You cannot update your own authority", HttpStatus.FORBIDDEN);

        Optional<User> u = userService.findByIdAndEnabledTrue(request.getUserId());
        if (u.isEmpty())
            return new ResponseEntity<String>("User not found or not enbled", HttpStatus.NOT_FOUND);

        Set<Authority> authorities = authorityService.findByAuthorityNameIn(request.getAuthorities());
        if (authorities.isEmpty())
            return new ResponseEntity<String>("Authorities not found", HttpStatus.NOT_FOUND);

        u.get().setAuthorities(authorities);

        return new ResponseEntity<String>("Authorities have been update", HttpStatus.OK);
    }

    /**
     * @param confirmCode
     * @return
     */
    @PatchMapping("confirm/{confirmCode}") // es.: localhost:8083/auth/confirm/3dn3f-3g5g3g35g-g3g35g-g3g3-g35ffsgr
    @Transactional
    public ResponseEntity<?> registrationConfirm(@PathVariable @NotBlank String confirmCode) {

        Optional<User> u = userService.findByConfirmCode(confirmCode);
        if (u.isEmpty())
            return new ResponseEntity<String>("User not found or..", HttpStatus.NOT_FOUND);

        u.get().setEnabled(true);
        u.get().setConfirmCode(null);

        Optional<Authority> authority = authorityService.findByAuthorityName("ROLE_READER");
        if (u.isEmpty())
            return new ResponseEntity<String>("Authority not Found", HttpStatus.NOT_FOUND);
        // Set<Authority> as = new HashSet<>();
        // as.add(authority.get());
        // ma meglio ridurre tutto ad una riga
        u.get().setAuthorities(Collections.singleton(authority.get()));

        return new ResponseEntity<String>("Your registration has been confirmed, please login", HttpStatus.OK);
    }
}

