package org.ahv.passwordprotectorback.exchange.response.type;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasicTypeResponse {
    private String id;
    private String name;
}
