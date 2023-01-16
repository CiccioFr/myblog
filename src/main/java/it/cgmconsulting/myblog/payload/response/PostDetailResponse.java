package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime;
    private String username; // author
    private double average;
}
