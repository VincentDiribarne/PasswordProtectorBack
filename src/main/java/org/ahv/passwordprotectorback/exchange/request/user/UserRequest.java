package org.ahv.passwordprotectorback.exchange.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest extends UserUpdateRequest {
    @NotBlank
    private String password;
}
