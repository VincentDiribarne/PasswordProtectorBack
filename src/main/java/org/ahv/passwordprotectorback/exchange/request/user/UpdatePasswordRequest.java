package org.ahv.passwordprotectorback.exchange.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    private String password;
}
