package org.ahv.passwordprotectorback.model;

import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.model.persistentEntity.PersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class User extends PersistentEntity {
    private String lastName;
    private String firstName;
    private String username;
    private String email;
    private String password;
    private int elementCount;
}
