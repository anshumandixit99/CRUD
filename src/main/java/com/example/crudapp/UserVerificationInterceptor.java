package com.example.crudapp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserVerificationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception{

        String token = request.getHeader("Authorization");

            if(!userService.isUserVerified(token)){
                response.sendError(HttpStatus.FORBIDDEN.value(), "User is not verified");
                return false;
            }
            return true;
    }


}
