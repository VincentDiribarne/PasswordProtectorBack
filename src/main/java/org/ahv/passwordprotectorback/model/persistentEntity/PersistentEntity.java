package org.ahv.passwordprotectorback.model.persistentEntity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Getter
@Setter
public class PersistentEntity {
    @Id
    private String id;

    private LocalDate creationDate;
    private LocalDate modificationDate;
}
