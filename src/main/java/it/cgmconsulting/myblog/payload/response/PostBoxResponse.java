package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostBoxResponse {

    // qua non riesce a recuperare il valore..
//    @Value("${post.path}")
//    private String imagePath;

    private long id;
    private String title;
    private String image;
}
