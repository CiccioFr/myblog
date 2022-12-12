package it.cgmconsulting.myblog.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.Objects;

@Entity
//bisogna sapere la fuonzione del DBMS per calcolare la lunghezza del carattere
//@Check(constraints = "LENGTH(title) > 10")
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 100)
    private String title;

    @Column(nullable = false)
    private String overview;

    // di default userebbe VARCHAR (255)
    // impostare a 64k la dimensione, essento su DB un text)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String image;

    private boolean published = false;

    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    private User authot;

    public Post(String title, String overview, String content, User authot) {
        this.title = title;
        this.overview = overview;
        this.content = content;
        this.authot = authot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
