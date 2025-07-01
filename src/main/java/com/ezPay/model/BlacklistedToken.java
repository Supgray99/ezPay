package com.ezPay.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false)
    private Date expiryDate;

    public BlacklistedToken() {}

    public BlacklistedToken(String jti, Date expiryDate) {
        this.jti = jti;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
}
