package org.ahv.passwordprotectorback.exchange.response.element;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasicElementResponse {
    private String name;
    private String url;
    private int passwordCount;
}
