package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.payload.response.PostBoxResponse;
import it.cgmconsulting.myblog.payload.response.PostSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // verifichiamo se esiste il titolo richiesto da PostController

    Optional<Post> findByIdAndPublishedTrue(long id);

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

    // uso di JPQL -> LIMIT  non esiste perché non usato da tutti db relazionali
    // ergo:
    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.PostBoxResponse(" +
            "p.id, " +
            "p.title, " +
            "p.image) " +
            "FROM Post p " +
            "WHERE p.published = true " +
            "ORDER BY p.updatedAt DESC")
    List<PostBoxResponse> getPostBoxes();

    // long id, String title, String image, String author, LocalDateTime updatedAt
    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.PostSearchResponse(" +
            "p.id, " +
            "p.title, " +
            "p.image, " +
            "p.author.username, " +
            "p.updatedAt) " +
            "FROM Post p " +
            "WHERE p.published = true " +
            "AND p.title LIKE :keyword OR p.content LIKE :keyword " +
            "ORDER BY p.updatedAt DESC")
    List<PostSearchResponse> getPostSearchResponse(@Param("keyword") String keyword);

    // long id, String title, String image, String author, LocalDateTime updatedAt
    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.PostSearchResponse(" +
            "p.id, " +
            "p.title, " +
            "p.image, " +
            "p.author.username, " +
            "p.updatedAt) " +
            "FROM Post p " +
            "WHERE p.published = true " +
            "AND p.title LIKE :keyword OR p.content LIKE :keyword " +
            "ORDER BY p.updatedAt DESC")
    Page<PostSearchResponse> getPostSearchResponsePaged(Pageable pageable, @Param("keyword") String keyword);
}
