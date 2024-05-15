package org.ahv.passwordprotectorback.exchange.response.password;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponse {
    private String token;
    private String refreshToken;
}
