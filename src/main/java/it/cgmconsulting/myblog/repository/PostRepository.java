package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // verifichiamo se esiste il titolo richiesto da PostController

    // metodo derivato
    boolean existsByTitle(String title);

    // nei 2 seguenti viene restituita
    // - la stringa del titolo se esiste
    // - null se non esiste

    // JPQL - qui si fa una query secca e return string
    // il parametro che intenfico deve essere identico alla quesry :title
    @Query(value = "SELECT p.title FROM Post p WHERE p.title = :title")
    String getTitle(@Param("title") String title);

    // SQL nativo
    @Query(value = "SELECT title FROM post WHERE title = :title", nativeQuery = true)
    String getTitleSQL(@Param("title") String title);

}
