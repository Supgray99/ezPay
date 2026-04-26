package com.ezPay.service.impl;

import com.ezPay.dto.UserDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.User;
import com.ezPay.repository.UserRepository;
import com.ezPay.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerUser(UserDto userDto){
        if(userRepository.findByUsername(userDto.getUsername()).isPresent()){
            throw new RuntimeException("UserName already exists !!");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword()))
                .balance(0.0)
                .build();

        return userRepository.save(user);
    }

    public List<UserResponseDto> searchByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getBalance()
                ))
                .toList();
    }
}
