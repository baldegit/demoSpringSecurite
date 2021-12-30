package com.example.demo.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserNameAndPasswordAuthenticationRequest {

    private String username;
    private String password;


}
