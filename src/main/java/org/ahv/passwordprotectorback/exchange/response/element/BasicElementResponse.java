package org.ahv.passwordprotectorback.exchange.response.element;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class BasicElementResponse implements Serializable {
    private String id;
    private String name;
    private String url;
    private int passwordCount;
}
