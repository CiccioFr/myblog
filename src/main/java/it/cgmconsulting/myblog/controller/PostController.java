package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.payload.response.CommentResponse;
import it.cgmconsulting.myblog.payload.response.PostBoxResponse;
import it.cgmconsulting.myblog.payload.response.PostDetailResponse;
import it.cgmconsulting.myblog.payload.response.PostSearchResponse;
import it.cgmconsulting.myblog.security.CurrentUser;
import it.cgmconsulting.myblog.security.UserPrincipal;
import it.cgmconsulting.myblog.service.CategoryService;
import it.cgmconsulting.myblog.service.CommentService;
import it.cgmconsulting.myblog.service.FileService;
import it.cgmconsulting.myblog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("post")
@Validated
public class PostController {

    @Autowired
    PostService postService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    FileService fileService;
    @Autowired
    CommentService commentService;

    // recupero info per immagine del Post dal application.yaml
    @Value("${post.size}")
    private long size;
    @Value("${post.width}")
    private int width;
    @Value("${post.height}")
    private int height;
    @Value("${post.extensions}")
    private String[] extensions;

    /**
     * memorizziamo una risorsa
     *
     * @param request
     * @param userPrincipal
     * @return
     */
    @PutMapping
    // lo può fare chi ha ruolo di EDITOR
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    // @CurrentUser annotation custom - posso recuperare chi si è loggato
    public ResponseEntity<?> save(@Valid @RequestBody PostRequest request, @CurrentUser UserPrincipal userPrincipal) {

        // controllo preventivo sull'unicità del titolo
        if (postService.existsByTitle(request.getTitle()))
            return new ResponseEntity<String>("A post with title '" + request.getTitle() + "' is already present", HttpStatus.BAD_REQUEST);

        // istanziare un oggetto Post
        Post p = new Post(request.getTitle(), request.getOverview(), request.getContent(), new User(userPrincipal.getId()));
        // e lo Persisto (è singolo, uso il .save(), avrei potuto usare @Transactional
        postService.save(p);

        //return new ResponseEntity<Post>(p, HttpStatus.CREATED);
        return new ResponseEntity<String>("Nuovo Post creato [" + p.getId() + "]", HttpStatus.CREATED);
    }

    /**
     * Modifica del post
     *
     * @param postId
     * @param userPrincipal
     * @return
     */
    @PatchMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    @Transactional // è una comodità se uso solo 1 tabella, con più tabelle diviene obbligatorio per pulizia
    public ResponseEntity updatePost(@PathVariable long postId,
                                     @Valid @RequestBody PostRequest request,
                                     @CurrentUser UserPrincipal userPrincipal) {

        Optional<Post> p = postService.findById(postId);
        if (p.isEmpty())
            return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);

        // solo chi ha scritto il post può associare le categorie al post stesso
        if (p.get().getAuthor().getId() != userPrincipal.getId())
            return new ResponseEntity("You are not the author of this post.", HttpStatus.FORBIDDEN);

        // controllo preventivo sull'unicità del titolo
        if (postService.existsByTitle(request.getTitle()))
            return new ResponseEntity("Title already exists", HttpStatus.BAD_REQUEST);
        else //if (title != null)
            p.get().setTitle(request.getTitle());

        if (!p.get().getOverview().equals(request.getOverview()))
            p.get().setOverview(request.getOverview());

        if (!p.get().getContent().equalsIgnoreCase(request.getContent()))
            p.get().setContent(request.getContent());

        p.get().setPublished(false);

        return new ResponseEntity("Post updated", HttpStatus.OK);
    }

    /**
     * Aggiunge categorie al Post
     * ad un secondo passaggio, elimina la categoria non più presente in elenco Set passato,
     * e fa insert di nuove cat
     *
     * @param postId
     * @param categories
     * @return
     */
    @PatchMapping("/add-categories/{postId}")
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity addCategoriesToPost(@PathVariable long postId,
                                              @RequestParam Set<String> categories,
                                              @CurrentUser UserPrincipal userPrincipal) {

        Optional<Post> p = postService.findById(postId);
        if (p.isEmpty())
            return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);

        // solo chi ha scritto il post può associare le categorie al post stesso
        if (p.get().getAuthor().getId() != userPrincipal.getId())
            return new ResponseEntity("You are not the author of this post.", HttpStatus.FORBIDDEN);

        Set<Category> categoriesToAdd = categoryService.findByVisibleTrueAndCategoryNameIn(categories);
        if (categoriesToAdd.isEmpty())
            return new ResponseEntity("Categories not selected/found", HttpStatus.NOT_FOUND);

        p.get().setCategories(categoriesToAdd);
        postService.save(p.get());

        return new ResponseEntity("Categories added to post " + p.get().getTitle(), HttpStatus.OK);
    }

    /**
     * Pubblicazione del Post
     * L'Admin convalida e rende visibile un Post
     *
     * @param postId
     * @return
     */
    @PatchMapping("/publish/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity publishPost(@PathVariable long postId) {

        Optional<Post> p = postService.findById(postId);
        if (p.isEmpty())
            return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);
        p.get().setPublished(true);

        return new ResponseEntity("Post" + p.get().getTitle() + " have been updated and Published", HttpStatus.OK);
    }

    /**
     * Carica ed associa immagine al post
     *
     * @param postId
     * @param userPrincipal
     * @param file
     * @return
     */
    @PatchMapping("/add-imase/{postId}")
    @PreAuthorize("hasRole('ROLE_EDITOR')")
    public ResponseEntity addImage(@PathVariable long postId,
                                   @CurrentUser UserPrincipal userPrincipal,
                                   // interfaccia di spring, è la rappresentazione del file che viene inviata
                                   // (in upload) in una request,
                                   // salva il file in memoria, dopo di che o lo persiste su rete o su DB
                                   @RequestParam MultipartFile file) {

        if (!fileService.checkSize(file, size))
            return new ResponseEntity("File empty or size great then " + size, HttpStatus.BAD_REQUEST);

        if (!fileService.checkDimension(fileService.fromMultipartFileToBufferedImage(file), width, height))
            return new ResponseEntity("Wrong width or height image", HttpStatus.BAD_REQUEST);

        // per le estensioni, ci passa lui il metodo lungo e rognoso, affinato nei vari corsi
        if (!fileService.checkExtension(file, extensions))
            return new ResponseEntity("File type not allowed", HttpStatus.BAD_REQUEST);

        Optional<Post> p = postService.findById(postId);
        if (p.isEmpty())
            return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);

        String imageToUpload = fileService.uploadPostImage(file, postId, p.get().getImage());
        if (imageToUpload == null)
            return new ResponseEntity("Something went wrong uploading image", HttpStatus.INTERNAL_SERVER_ERROR);

        p.get().setImage(imageToUpload);
        return new ResponseEntity("Image " + imageToUpload + " Successfully uploaded", HttpStatus.OK);
    }

    @GetMapping("/public/{item}")
    public ResponseEntity getBoxesInHomePage(@PathVariable int item) {
        List<PostBoxResponse> boxes = postService.getPostBoxes(item);
        return new ResponseEntity(boxes, HttpStatus.OK);
    }

    /**
     * ricerca di Posts con Paginazione
     *
     * @param keyword
     * @param pageNumber
     * @param pageSize
     * @param direction
     * @param sortBy
     * @return
     */
    @GetMapping("/public/search")
    public ResponseEntity getPostSearchResponse(@RequestParam @NotBlank String keyword,
                                                @RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "2") int pageSize,
                                                @RequestParam(defaultValue = "DESC") String direction,
                                                @RequestParam(defaultValue = "updatedAt") String sortBy) {
        List<PostSearchResponse> result = postService.getPostSearchResponsePaged(pageNumber, pageSize, direction, sortBy, keyword);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * ricerca di Postscon voto Medio
     *
     * @param postId
     * @return
     */
    @GetMapping("/public/detail/{postId}")
    public ResponseEntity getPostDetail(@PathVariable long postId){
        PostDetailResponse pdr = postService.getPostDetailResponse(postId);

        if(pdr == null)
            return new ResponseEntity("Post not found", HttpStatus.NOT_FOUND);

        List<CommentResponse> comments = commentService.getComments(postId);
        pdr.setComments(comments);

        return new ResponseEntity(pdr, HttpStatus.OK);
    }

}
