package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
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
    private List<CommentResponse> comments;

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
