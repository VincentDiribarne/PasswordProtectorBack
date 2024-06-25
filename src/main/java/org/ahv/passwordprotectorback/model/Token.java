package org.ahv.passwordprotectorback.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document
@Builder
public class Token {
    private String userID;
    private String token;
    private TokenType type;
    private Date expirationDate;
    private boolean alreadyUsed;
}
