package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.payload.response.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

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

    Set<Category> findByVisibleTrueAndCategoryNameIn(Set<String> categories);

    /*      NO  ERRATA
    @Query(value ="SELECT ps.category_name" +
            "FROM PostCategories pc, Category c" +
            "WHERE ps.post_id=:postId " +
            "AND cs.category_name=c=category_name" +
            "AND c.visibile=true  ",nativeQuery = true )
    Set<String> getCategoriesNameByPost(@Param("postId") long id);
     */

    /*
    @Query(value = "SELECT pc.category_name " +
            "FROM post_categories pc " +
            "INNER JOIN category c ON c.category_name=pc.category_name " +
            "WHERE c.visible=true  " +
            "AND pc.post_id = :postId", nativeQuery = true)
    Set<String> getCategoriesNameByPost(@Param("postId") long id);
    */

    @Query(value = "SELECT cs.categoryName " +
            "FROM Post p " +
            "LEFT JOIN p.categories cs " +
            "WHERE p.id = :postId AND cs.visible = true")
    Set<String> getCategoriesNameByPost(@Param("postId") long id);
}
