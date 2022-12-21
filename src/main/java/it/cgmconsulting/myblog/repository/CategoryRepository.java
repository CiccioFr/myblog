package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.payload.response.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    // uso dei metodi derivati (la piu lenta)
    List<Category> findByVisibleTrue();

    // JPQL: JavaPersistent Query Language - nome del tutto arbitrario
    // @Query per immettere una query usando la sintassi jpql
    // - fa riferimento a nome delle classi ed attributi
    @Query(value = "SELECT c.categoryName FROM Category c WHERE c.visible=true ORDER BY c.categoryName")
    List<String> getByVisibleTrue();

    // Per fare una SELECT su più attributi: fare una classe custom in response
    // (usato per generare il Json per response: key -> suoi attributi)
    // in SELECT serve un nuovo oggetto basato su CategoryResponse che prende i dati dall'Entity Category
    // i dati: tipi ed ordine nella query devono corrispondere agli attributi di CategoryResponse
    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.CategoryResponse(" +
            "c.categoryName, " +
            "c.visible" +
            ") FROM Category c " +
            "WHERE c.visible = true ORDER BY c.categoryName")
    List<CategoryResponse> getCategoryByVisibleTrue();
    // poi nel service
    // conviene 3 motivi
    // - non servono accrocchi strani per mappare
    // - viene controllata all'inizio (strutturalmente corretta
    // - velocità di esecuzione
    // - maggiore portabilità in caso di cambio DB
    // SVANTGGI
    // alcuni comandi non accettati (es. limit non esiste in JPQL)

    // SQL standard:
    // non posso mappare
    // nella query si fa riferimento a tabelle e suoi attributi
    // se devo passare tutti gli attributi usare *, scrivendoli si incazza,  (category_name, visible -> * )
    @Query(value = "SELECT * FROM category WHERE visible = 1 ORDER BY category_name", nativeQuery = true)
    List<Category> getCategoryByVisibleTrueSQL();
}
