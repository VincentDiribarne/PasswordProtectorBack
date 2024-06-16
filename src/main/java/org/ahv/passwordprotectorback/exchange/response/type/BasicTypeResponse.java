package org.ahv.passwordprotectorback.exchange.response.type;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class BasicTypeResponse implements Serializable {
    private String id;
    private String name;
}
