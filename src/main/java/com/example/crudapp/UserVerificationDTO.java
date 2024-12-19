package com.example.crudapp;

import lombok.Data;

@Data
public class UserVerificationDTO {
    private long id;
    private String otp;
    private String email;
    private String password;

}

