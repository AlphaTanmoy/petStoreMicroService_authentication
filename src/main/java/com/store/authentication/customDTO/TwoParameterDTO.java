package com.store.authentication.customDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoParameterDTO <T, U> {
    private T firstParameter;
    private U secondParameter;

    public TwoParameterDTO() {
    }

    public TwoParameterDTO(T firstParameter) {
        this.firstParameter = firstParameter;
    }

    public TwoParameterDTO(T firstParameter, U secondParameter) {
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
    }

    @Override
    public String toString() {
        return "TwoParameterDTO{" +
                "firstParameter=" + firstParameter +
                ", secondParameter=" + secondParameter +
                '}';
    }
}