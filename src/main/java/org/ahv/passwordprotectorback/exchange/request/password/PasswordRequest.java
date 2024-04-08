package org.ahv.passwordprotectorback.exchange.request.password;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    private String password;

    private String comment;

    @NotBlank
    private String elementID;
}