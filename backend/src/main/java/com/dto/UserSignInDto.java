package com.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserSignInDto {
    @NotBlank
    @Length(min = 4)
    private String username;

    @NotBlank
    private String password;
}
