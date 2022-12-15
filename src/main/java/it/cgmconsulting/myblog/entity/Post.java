package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.*;

@Entity
//bisogna sapere la fuonzione del DBMS per calcolare la lunghezza del carattere
//@Check(constraints = "LENGTH(title) > 10")
@Getter
@Setter
@NoArgsConstructor
public class Post extends CreationUpdate{

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

    // piu post possono essere scritti da un autore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User authot;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_categories",
            joinColumns = {@JoinColumn(name = "post_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "category_name", referencedColumnName = "categoryName")}
    )
    Set<Category> categories = new HashSet();

    // mappedBy = "post" nome tabella - mappiamo in riferimento alla tabella
    // orphanRemoval = true - cancellazione in cascata dei commenti legati al post
    // cascade = CascadeType.ALL -
    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    List<Comment> comments = new ArrayList();

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
