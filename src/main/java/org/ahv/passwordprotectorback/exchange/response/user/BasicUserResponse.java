package org.ahv.passwordprotectorback.exchange.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BasicUserResponse {
    private String username;
    private int elementCount;
}
