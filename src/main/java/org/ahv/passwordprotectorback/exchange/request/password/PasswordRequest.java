package org.ahv.passwordprotectorback.exchange.request.password;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequest extends PasswordUpdateRequest{
    @NotBlank
    private String elementID;
}