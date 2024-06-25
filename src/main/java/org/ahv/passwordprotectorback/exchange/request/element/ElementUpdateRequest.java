package org.ahv.passwordprotectorback.exchange.request.element;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementUpdateRequest {
    private String name;
    private String url;

    private String description;
    private String typeID;
}
