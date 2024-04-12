package org.ahv.passwordprotectorback.exchange.request.element;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementRequest extends ElementUpdateRequest {
    @NotBlank
    private String username;
}