package com.example.crudapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String otp;
    private Boolean isVerified;
    private LocalDateTime otpExpirationTime;
    private String jwtToken;

    @JsonIgnore
    private String password;



}
