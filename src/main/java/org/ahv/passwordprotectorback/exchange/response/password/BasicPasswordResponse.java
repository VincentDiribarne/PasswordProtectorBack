package org.ahv.passwordprotectorback.exchange.response.password;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class BasicPasswordResponse implements Serializable {
    private String id;
    private String identifier;
    private String comment;
}
