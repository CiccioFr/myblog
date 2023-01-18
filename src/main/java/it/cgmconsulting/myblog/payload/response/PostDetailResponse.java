package it.cgmconsulting.myblog.payload.response;

import it.cgmconsulting.myblog.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO ??
 * post completo: Dettagli del post + avg(Rating) + Commenti
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private long id;
    private String title;
    private String content;
    private String image;
    private LocalDateTime updatedAt;
    private String username; // author
    private double average;
    // inizializzioamo  a vuoto : meglio che se per qualche errore arriva a front end un null
    private List<CommentResponse> comments = new ArrayList();
    private Set<String> categories = new HashSet<String>();

    public PostDetailResponse(long id, String title, String content, String image, LocalDateTime updatedAt, String username, double average) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.updatedAt = updatedAt;
        this.username = username;
        this.average = average;
    }
}
