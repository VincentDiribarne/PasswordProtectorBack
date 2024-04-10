package org.ahv.passwordprotectorback.exchange.response.type;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TypeResponse {
    private String id;
    private String name;
    private BasicUserResponse user;
    private LocalDate creationDate;
    private LocalDate modificationDate;
}
