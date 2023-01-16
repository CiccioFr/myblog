package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.payload.response.PostBoxResponse;
import it.cgmconsulting.myblog.payload.response.PostDetailResponse;
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

    /* verifica esistenza title nei tre modi */
    // viene restituito:
    // - la stringa del titolo se esiste
    // - null se non esiste

    // metodo derivato
    boolean existsByTitle(String title);

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

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.PostSearchResponse(" +
            "p.id, " +
            "p.title, " +
            "p.image, " +
            "p.author.username, " +
            "p.updatedAt) " +
            "FROM Post p " +
            "WHERE p.published = true " +
            "AND p.title LIKE :keyword OR p.content LIKE :keyword " +
            "ORDER BY p.updatedAt DESC",
            // page ha 2 metodi ma non riesce a calcolarsi il nr di elementi (la size)
            countQuery = "SELECT COUNT(p) from Post p WHERE p.published=true AND p.title LIKE :keyword OR p.content LIKE :keyword")
    Page<PostSearchResponse> getPostSearchResponsePaged(Pageable pageable, @Param("keyword") String keyword);

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.PostSearchResponse(" +
            // i campi devono corrispondere al costruttore
            "p.id, " +
            "p.title, " +
            "p.content, " +
            "p.image," +
            "p.updatedAt, " +
            "p.author.username, " +
            //"AVG(voto.rate)) AS average " +
            "(SELECT COALESCE(ROUND(AVG(r.rate), 2), 0d) FROM Rate r WHERE r.ratingId.post.id = p.id) AS average) " +
            "FROM Post p " +
            //"LEFT JOIN Rating voto ON p.id = voto.ratingId.post.id " +
            "WHERE p.published = true AND p.id = :id")
    PostDetailResponse getPostDetailResponse(@Param("id") long id);
}
