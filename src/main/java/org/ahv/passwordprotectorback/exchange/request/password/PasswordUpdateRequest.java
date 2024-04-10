package org.ahv.passwordprotectorback.exchange.request.password;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    private String password;

    private String comment;
}
