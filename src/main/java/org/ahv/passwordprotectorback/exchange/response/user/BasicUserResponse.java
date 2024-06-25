package org.ahv.passwordprotectorback.exchange.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class BasicUserResponse implements Serializable {
    private String username;
    private int elementCount;
}
