package org.ahv.passwordprotectorback.exchange.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserConnectRequest {
    private String username;
    private String password;
}
