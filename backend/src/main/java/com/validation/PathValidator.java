package com.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PathValidator implements ConstraintValidator<ValidPath, String> {

    @Override
    public boolean isValid(String path, ConstraintValidatorContext constraintValidatorContext) {
        if(path == null || path.isBlank()) {
            return false;
        }

        String[] segments = path.split("/");

        for(int i = 0; i < segments.length; i++) {
            String segment = segments[i];

            if(segment.isEmpty()) {
                if(i == segments.length - 1) {
                    continue;
                }
                return false;
            }
            if(segment.equals(".")) return false;
        }

        return true;
    }
}
