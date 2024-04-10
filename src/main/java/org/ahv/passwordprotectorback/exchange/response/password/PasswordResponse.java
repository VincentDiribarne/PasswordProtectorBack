package org.ahv.passwordprotectorback.exchange.response.password;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PasswordResponse {
    private String id;
    private String identifier;
    private String comment;
    private BasicElementResponse element;
    private LocalDate creationDate;
    private LocalDate modificationDate;
}
