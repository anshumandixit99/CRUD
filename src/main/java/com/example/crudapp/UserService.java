package com.example.crudapp;

import com.example.crudapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRATION_MINUTES = 5;

    //create user with DTO
    public UserCreationDTO createUser(UserCreationDTO userDTO){
        Users user = new Users();

        user.setName(userDTO.getName());

        user.setEmail(userDTO.getEmail());

        log.info(userDTO.getPassword());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setIsVerified(false);

        user.setOtp(generateOtp());
        user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));

        String jwtToken = JwtUtils.generateJwtToken(user.getEmail());
        user.setJwtToken(jwtToken);

        Users savedUser = userRepository.save(user);

        return new UserCreationDTO(savedUser.getName(),savedUser.getEmail(),
                savedUser.getOtp(),savedUser.getPassword(), savedUser.getJwtToken());
    }



    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6 digit OTP
        return String.valueOf(otp);
    }

    //verify otp

    public boolean verifyOtp(UserVerificationDTO verificationDTO){
        Optional<Users> optionalUser = userRepository.findByEmail(verificationDTO.getEmail());

        if (optionalUser.isPresent()){
            Users user = optionalUser.get();
            if (user.getOtp().equalsIgnoreCase(verificationDTO.getOtp()) &&
                    LocalDateTime.now().isBefore(user.getOtpExpirationTime())) {
                user.setIsVerified(true);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public boolean isUserVerified(String token){
        Optional<Users> optionalUser = userRepository.findByjwtToken(token);
        return optionalUser.map(Users::getIsVerified).orElse(false);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserbyemail(String id) {
        return userRepository.findByEmail(id);
    }

    public Users updateUser(Users existingUser, UserCreationDTO request){
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());

        return userRepository.save(existingUser);
    }

    public void deleteUser(String email) throws UserPrincipalNotFoundException {
        Users user = userRepository.findByEmail(email).orElseThrow(
                ()->new UserPrincipalNotFoundException("User not found with this email "+email));
        userRepository.delete(user);

    }






}