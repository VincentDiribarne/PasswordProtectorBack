package org.ahv.passwordprotectorback.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.model.persistentEntity.PersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@Builder
public class Password extends PersistentEntity {
    private String identifier;
    private String password;
    private String comment;
    private String elementID;
}
