package com.store.authentication.customDTO;

public class ThreeParameterDTO <T, U, V> {
    private T firstParameter;
    private U secondParameter;
    private V thirdParameter;

    public ThreeParameterDTO() {
    }

    public ThreeParameterDTO(T firstParameter) {
        this.firstParameter = firstParameter;
    }

    public ThreeParameterDTO(T firstParameter, U secondParameter) {
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
    }

    public ThreeParameterDTO(T firstParameter, U secondParameter, V thirdParameter) {
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
        this.thirdParameter = thirdParameter;
    }

    @Override
    public String toString() {
        return "TwoParameterDTO{" +
                "firstParameter=" + firstParameter +
                ", secondParameter=" + secondParameter +
                ", thirdParameter=" + thirdParameter +
                '}';
    }
}