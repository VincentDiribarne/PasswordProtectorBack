package org.ahv.passwordprotectorback.exchange.request.element;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementUpdateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String url;

    private String description;
    private String typeID;
}
