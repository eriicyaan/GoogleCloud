package com.service;

import com.dto.UserCreateEditDto;
import com.dto.UserReadDto;
import com.entity.User;
import com.exception.UsernameAlreadyExistsException;
import com.mapper.UserCreateEditMapper;
import com.mapper.UserReadMapper;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserReadMapper userReadMapper;

    public UserReadDto create(UserCreateEditDto userCreateEditDto) {
        User maybeSameUsername = userRepository.findByUsername(userCreateEditDto.getUsername());

        if (maybeSameUsername != null) {
            throw new UsernameAlreadyExistsException("user with username " + maybeSameUsername.getUsername() +" already exists");
        }

        return Optional.of(userCreateEditDto)
                .map(userCreateEditMapper::map)
                .map(userRepository::save)
                .map(userReadMapper::map)
                .orElseThrow();
    }


    public UserReadDto findByUsername(String username) {
        return Optional.of(userRepository.findByUsername(username))
                .map(userReadMapper::map)
                .orElseThrow();
    }


    @Override
    public UserDetails loadUserByUsername(String username){
        return Optional.of(userRepository.findByUsername(username))
                .map(user ->
                        new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                Collections.singleton(user.getRole()))
                        )
                .orElseThrow();

    }
}
