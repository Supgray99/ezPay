package com.ezPay.controller;

import com.ezPay.dto.LoginRequestDto;
import com.ezPay.dto.AuthTokenResponseDto;
import com.ezPay.model.BlacklistedToken;
import com.ezPay.repository.BlacklistedTokenRepository;
import com.ezPay.service.TokenBlacklistService;
import com.ezPay.util.TokenProvider;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

//    @Autowired
//    private BlacklistedTokenRepository blacklistRepo;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthTokenResponseDto(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = header.substring(7); // Strip "Bearer "
        String jti = tokenProvider.extractTokenId(token);
//        Date expiry = Jwts.parserBuilder()
//                .setSigningKey("mySecretKey1234567890mySecretKey1234567890".getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getExpiration();

        long expirationSeconds = tokenProvider.getExpirationDuration(token);

        tokenBlacklistService.blacklistToken(jti, expirationSeconds);

        //blacklistRepo.save(new BlacklistedToken(jti, expiry));

        return ResponseEntity.ok("Logged out successfully.");
    }
}
