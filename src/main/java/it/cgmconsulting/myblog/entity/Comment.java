package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.Creation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment extends Creation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String comment;

    // nuovo argomento - FetchType: EAGER | LAZY
    // EAGER è di default per tutte le relazioni ..ToOne e porta dietro tutto l'oggetto
    // LAZY: dobbiamo impostare che porti dietro solo id e non tutto l'oggetto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private boolean censored = false;

    public Comment(long id) {
        this.id = id;
    }

    public Comment(String comment, User author, Post post) {
        this.comment = comment;
        this.author = author;
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
