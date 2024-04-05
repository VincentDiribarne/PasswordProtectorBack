package org.ahv.passwordprotectorback.model;

import org.ahv.passwordprotectorback.model.persistentEntity.PersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Element extends PersistentEntity {
    private String name;
    private String url;
}