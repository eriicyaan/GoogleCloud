package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReadDto {
    private Long id;
    private String username;
    private String email;
    private LocalDate birthday;
}
