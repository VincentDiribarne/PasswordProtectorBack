package org.ahv.passwordprotectorback.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document
@Builder
public class Token {
    private String userID;
    private String token;
    private LocalDate expirationDate;
    private boolean alreadyUsed;
}
