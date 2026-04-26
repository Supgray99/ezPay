package com.ezPay.service;

import com.ezPay.dto.UserDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.User;

import java.util.List;

public interface UserService {
    User registerUser(UserDto userDto);

    List<UserResponseDto> searchByUsername(String username);
}
