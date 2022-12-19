package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.PostService;
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
@RequestMapping("post")
public class PostController {

    @Autowired PostService postService;

    /**
     *  memorizziamo una risorsa
     *
     * @param request
     * @param userPrincipal
     * @return
     */
    @PutMapping
    // lo può fare chi ha ruolo di EDITOR
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    // @CurrentUser annotation custom - posso recuperare chi si è loggato
    public ResponseEntity<?> save(@Valid @RequestBody PostRequest request, @CurrentUser UserPrincipal userPrincipal){

        // controllo preventivo sull'unicità del titolo
        if(postService.existsByTitle(request.getTitle()))
            return new ResponseEntity<String>("A post with title '"+request.getTitle()+"' is already present", HttpStatus.BAD_REQUEST);

        // istanziare un oggetto Post
        Post p = new Post(request.getTitle(), request.getOverview(), request.getContent(), new User(userPrincipal.getId()));
        postService.save(p);

        //return new ResponseEntity<Post>(p, HttpStatus.CREATED);
        return new ResponseEntity<String>("Nuovo Post creato ["+p.getId()+"]", HttpStatus.CREATED);
    }
}
