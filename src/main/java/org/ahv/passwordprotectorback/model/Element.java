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
public class Element extends PersistentEntity {
    private String name;
    private String url;
    private String description;
    private int passwordCount;
    private String userID;
    private String typeID;
}