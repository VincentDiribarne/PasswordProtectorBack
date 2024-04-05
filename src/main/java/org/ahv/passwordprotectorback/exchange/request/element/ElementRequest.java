package org.ahv.passwordprotectorback.exchange.request.element;

import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordRequest;

import java.util.List;

@Getter
@Setter
public class ElementRequest {
    private String name;
    private String url;
    private String description;
    private List<PasswordRequest> passwords;
}