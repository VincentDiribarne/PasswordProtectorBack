package org.ahv.passwordprotectorback.exchange.response.password;

import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PasswordResponse {
    private String identifier;
    private String comment;
    private BasicUserResponse user;
    private BasicElementResponse element;
}
