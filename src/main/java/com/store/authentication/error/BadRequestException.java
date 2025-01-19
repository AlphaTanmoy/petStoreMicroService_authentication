package com.store.authentication.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BadRequestException extends RuntimeException {

    private String errorMessage = "";
    private Integer code;
    private String type;

    public BadRequestException(String s) {
        this.errorMessage = s;
    }
}
