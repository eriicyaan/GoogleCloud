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
                .username(object.getUsername())
                .password(passwordEncoder.encode(object.getPassword()))
                .role(Role.USER)
                .build();
    }
}
