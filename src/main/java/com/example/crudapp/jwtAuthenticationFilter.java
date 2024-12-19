package com.example.crudapp;

import com.example.crudapp.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class jwtAuthenticationFilter  extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer")){
            token = token.replace("Bearer ","");

            if(JwtUtils.validateJwtToken(token)){
                String username = JwtUtils.getUsernameFromJwtToken(token);

                Users user = userRepository.findByEmail(username).orElse(null);
                if (user!=null && user.getIsVerified()){
                    SecurityContextHolder.getContext().setAuthentication(new
                            UsernamePasswordAuthenticationToken(username,null,null));
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
