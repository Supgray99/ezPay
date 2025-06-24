package com.ezPay.service;

import com.ezPay.dto.UserDto;
import com.ezPay.model.User;

public interface UserService {
    User registerUser(UserDto userDto);
}
