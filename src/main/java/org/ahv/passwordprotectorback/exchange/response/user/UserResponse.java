package org.ahv.passwordprotectorback.exchange.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;

import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private List<BasicElementResponse> elements;
    private int elementCount;
}
