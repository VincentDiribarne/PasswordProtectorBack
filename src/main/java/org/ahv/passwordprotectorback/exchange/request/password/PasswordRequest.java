package org.ahv.passwordprotectorback.exchange.request.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequest {
    private String identifier;
    private String password;
    private String comment;
}