package org.ahv.passwordprotectorback.exchange.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    private String id;

    private String token;

    private String refreshToken;
}
