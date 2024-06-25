package org.ahv.passwordprotectorback.exchange.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class BasicResponse implements Serializable {
    private String message;
}
