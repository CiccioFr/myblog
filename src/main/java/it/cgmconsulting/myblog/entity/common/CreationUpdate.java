package it.cgmconsulting.myblog.entity.common;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class CreationUpdate extends Creation implements Serializable {

    // Contrassegna una proprietà come timestamp di aggiornamento dell'entità che la contiene.
    // Il valore della proprietà verrà impostato sulla data corrente ogni qual volta che l'entità proprietaria viene aggiornata.
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
