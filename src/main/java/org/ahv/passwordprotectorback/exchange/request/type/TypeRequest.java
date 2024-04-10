package org.ahv.passwordprotectorback.exchange.request.type;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String userID;
}
