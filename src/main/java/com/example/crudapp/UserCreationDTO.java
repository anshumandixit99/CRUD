package com.example.crudapp;

import lombok.Data;

@Data
public class UserCreationDTO {
    public String email;
    public String name;

   // @JsonIgnore
    public String password;

    public String otp;
    public String jwt;

    public UserCreationDTO(String name, String email, String otp,String password,String jwt) {
        this.name = name;
        this.email = email;
        this.otp = otp;
        this.password = password;
        this.jwt=jwt;
    }
}
