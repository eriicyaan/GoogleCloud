package com.mapper;

import com.dto.UserCreateEditDto;
import com.entity.Role;
import com.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreateEditMapper implements Mapper<UserCreateEditDto, User>{

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateEditDto object) {
        return User.builder()
                .email(object.getEmail())
                .username(object.getUsername())
                .password(passwordEncoder.encode(object.getPassword()))
                .email(object.getEmail())
                .birthday(object.getBirthday())
                .role(Role.USER)
                .build();
    }
}
