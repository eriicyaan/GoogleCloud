package com.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserCreateEditDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 4, max = 20)
    private String username;

    @NotBlank
    @Length(min = 5, max = 20)
    private String password;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
