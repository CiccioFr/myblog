package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@RestController
@RequestMapping("category")
@Validated
public class CategoryController {

    // evita di dichiarare ed inizializzare un oggetto, lo rendo già disponibile con tutti i suoi metodi
    @Autowired
    CategoryService categoryService;

    @PutMapping("/{category}") // memoriziamo una risorsa sul server
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WRITER')")
    public ResponseEntity<?> save(@PathVariable @NotBlank @Size(max = 50, min = 2) String category) {
        // verificare che non esista già la categoria che andremo ad inserire
        // 1 creo interfaccia nel repository
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
    public ResponseEntity<?> switchVisibility(@PathVariable @NotBlank @Size(max = 50, min = 2) String category) {

        // verifichiamo che esista la categoria su cui modificare la visibilità
        Optional<Category> cat = categoryService.findById(category);
        if (!cat.isPresent())
            return new ResponseEntity<String>("Category not found", HttpStatus.NOT_FOUND);

        // switch visibily
        cat.get().setVisible(!cat.get().isVisible());

        categoryService.save(cat.get());

        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
