package it.cgmconsulting.myblog.controller;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import it.cgmconsulting.myblog.entity.Authority;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.request.SigninRequest;
import it.cgmconsulting.myblog.payload.request.SignupRequest;
import it.cgmconsulting.myblog.payload.response.JwtAuthenticationResponse;
import it.cgmconsulting.myblog.security.JwtTokenProvider;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.AuthorityService;
import it.cgmconsulting.myblog.service.UserService;

import java.util.Collections;
import java.util.Optional;


@RestController
@RequestMapping("auth") // localhost:{port}/auth/....
public class AuthController {

    @Autowired AuthenticationManager authenticationManager;
    @Autowired UserService userService;
    @Autowired AuthorityService authorityService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtTokenProvider tokenProvider;


    @PostMapping("signin")
    @Transactional
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest request) {
        Optional<User> u = userService.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail());
        if(!u.isPresent())
            return new ResponseEntity<String>("Bad Credentials", HttpStatus.FORBIDDEN);
        if(!passwordEncoder.matches(request.getPassword(), u.get().getPassword()))
            return new ResponseEntity<String>("Bad Credentials", HttpStatus.FORBIDDEN);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		request.getUsernameOrEmail(),
                		request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtTokenProvider.generateToken(authentication);
        JwtAuthenticationResponse currentUser = UserPrincipal.createJwtAuthenticationResponseFromUserPrincipal((UserPrincipal) authentication.getPrincipal(), jwt);

        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

    	if(userService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Username already in use", HttpStatus.BAD_REQUEST);
        }

        if(userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Email Address already in use!", HttpStatus.BAD_REQUEST);
        }

        // find default valid authority
        Optional<Authority> authority = authorityService.findByAuthorityName("ROLE_GUEST");

        // Creating user's account
        User user = new User(
    		signUpRequest.getUsername(),
            signUpRequest.getEmail().toLowerCase(),
            passwordEncoder.encode(signUpRequest.getPassword()),
            Collections.singleton(authority.get()) // transform object Authority into Set<Authority>
        );

        userService.save(user);

        return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }


}

