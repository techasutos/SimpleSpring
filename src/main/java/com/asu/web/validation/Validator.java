package com.asu.web.validation;

public class Validator {

    public static void validate(Object value) {

        if (value == null || value.toString().isEmpty()) {
            throw new RuntimeException("Validation failed");
        }
    }
}
