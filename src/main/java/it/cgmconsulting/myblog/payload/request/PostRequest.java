package it.cgmconsulting.myblog.payload.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class PostRequest {

    // Annotazioni di validazione
    @NotBlank @Size(max = 100, min = 3)
    private String title;

    @NotBlank @Size(max = 255, min = 15)
    private String overview;

    // max = deve corrispondere al max del DB
    @NotBlank @Size(max = 65535, min = 100)
    private String content;
}
