package org.ahv.passwordprotectorback.exchange.response.element;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.type.BasicTypeResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ElementResponse implements Serializable {
    private String id;
    private String name;
    private String url;
    private String description;
    private BasicUserResponse user;
    private List<BasicPasswordResponse> passwords;
    private int passwordCount;
    private BasicTypeResponse type;
    private LocalDate creationDate;
    private LocalDate modificationDate;
}
