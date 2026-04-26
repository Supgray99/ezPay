package com.ezPay.controller;


import com.ezPay.dto.UserDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.User;
import com.ezPay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserDto userDto){
        User user = userService.registerUser(userDto);

        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getBalance()
        );

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String username) {
        return ResponseEntity.ok(userService.searchByUsername(username));
    }

}
