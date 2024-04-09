package org.ahv.passwordprotectorback.exchange.response.element;

import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.type.TypeResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ElementResponse {
    private String id;
    private String name;
    private String url;
    private String description;
    private BasicUserResponse user;
    private List<BasicPasswordResponse> passwords;
    private int passwordCount;
    private TypeResponse type;
}
