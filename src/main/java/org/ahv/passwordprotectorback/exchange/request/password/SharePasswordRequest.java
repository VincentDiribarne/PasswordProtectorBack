package org.ahv.passwordprotectorback.exchange.request.password;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharePasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String passwordId;
}
