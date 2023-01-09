package it.cgmconsulting.myblog.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity @Getter @Setter @NoArgsConstructor
public class Avatar {
    // per scrivere una immagine su db servono essenzialmente 3 valori

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String filename;

    // mime type associato (NON L'ESTENSIONE) al file es.: "image/jpg", "image/gif", "image/png"
    @Column(nullable = false)
    private String filetype;

    // array di byte: come viene salvato il file su DB
    @Column(nullable = false)
    // sul db diventa un tipo C-Lob
    @Lob
    private byte[] data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return id == avatar.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
