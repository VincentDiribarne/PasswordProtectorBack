package org.ahv.passwordprotectorback.exchange.response.password;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;

@Getter
@Setter
@Builder
public class PasswordResponse {
    private String identifier;
    private String comment;
    private BasicElementResponse element;
}
