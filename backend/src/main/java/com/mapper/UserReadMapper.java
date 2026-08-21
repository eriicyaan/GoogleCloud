package com.mapper;

import com.dto.UserReadDto;
import com.entity.User;
import org.springframework.stereotype.Component;


@Component
public class UserReadMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User object) {
        return new UserReadDto(
                object.getId(),
                object.getUsername()
        );
    }
}
