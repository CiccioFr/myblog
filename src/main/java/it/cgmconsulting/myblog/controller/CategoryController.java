package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.payload.response.CategoryResponse;
import it.cgmconsulting.myblog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("category")
@Validated
public class CategoryController {

    // evita di inizializzare un oggetto, è così disponibile con tutti i suoi metodi
    @Autowired
    CategoryService categoryService;

    /**
     * Crea una Categoria
     * @param category
     * @return La Response di avvenuta registrazione
     */
    @PutMapping("/{category}") // memoriziamo una risorsa sul server
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WRITER')")
    public ResponseEntity<?> save(@PathVariable @NotBlank @Size(max = 50, min = 2) String category) {
        // verificare che non esista già la categoria che andremo ad inserire
        // - 1 creo interfaccia nel repository
        Optional<Category> cat = categoryService.findById(category);
        if (cat.isPresent())
            return new ResponseEntity<String>("Category already present", HttpStatus.BAD_REQUEST);

        // se non esiste, persisterla sul DB
        Category c = new Category(category);
        categoryService.save(c);

        return new ResponseEntity<String>("New category " + category + " added", HttpStatus.CREATED);

    }

    // attiviamo / disattiviamo la visibilità di una categoria
    @PatchMapping("/{category}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // non serve più fare la persistenza, ribalta in auto le modifiche (UpDate) sul DB
    // in alternativa a  categoryService.save(cat.get());
    // l'oggetto viene gestito totalmente da Hibernate
    // ha insito il RollBack in caso di eccezioni
    @Transactional
    public ResponseEntity<?> switchVisibility(@PathVariable @NotBlank @Size(max = 50, min = 2) String category) {

        // verifichiamo che esista la categoria su cui modificare la visibilità
        Optional<Category> cat = categoryService.findById(category);
        if (!cat.isPresent())
            return new ResponseEntity<String>("Category not found", HttpStatus.NOT_FOUND);

        // switch visibily
        cat.get().setVisible(!cat.get().isVisible());

        // il dato è in stato Detach
        // persistenza del dato da usare senza @Transactional
        // categoryService.save(cat.get());

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllCategories() {
        // se in DB non trova nulla, restituisce una List vuota (e non un null) (perchè il DB restituisce lista)
        List<Category> categories = categoryService.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<?> getVisibleCategories() {
        // se in DB non trova nulla, restituisce una List vuota (e non un null) (perchè il DB restituisce lista)
        List<Category> categories = categoryService.findByVisibleTrue();
        return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
    }

    @GetMapping("/public/jpql")
    public ResponseEntity<?> getCategoriesJPQL() {
        List<String> categories = categoryService.getByVisibleTrue();
        return new ResponseEntity<List<String>>(categories, HttpStatus.OK);
    }

    @GetMapping("/public/jpql2")
    public ResponseEntity<?> getCategoriesJPQL2() {
        List<CategoryResponse> categories = categoryService.getCategoryByVisibleTrue();
        return new ResponseEntity<List<CategoryResponse>>(categories, HttpStatus.OK);
    }

    @GetMapping("/public/sql")
    public ResponseEntity<?> getCategoriesSQL() {
        List<Category> categories = categoryService.getCategoryByVisibleTrueSQL();
        return new ResponseEntity<List<CategoryResponse>>(categories, HttpStatus.OK);
    }
}
