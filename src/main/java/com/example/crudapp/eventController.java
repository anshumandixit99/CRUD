package com.example.crudapp;

import com.example.crudapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/events")
public class eventController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/new")
    public ResponseEntity<UserCreationDTO> createUser(@ModelAttribute UserCreationDTO userDto) {
        UserCreationDTO createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Verify OTP endpoint
    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody UserVerificationDTO verificationDto) {
        boolean isVerified = userService.verifyOtp(verificationDto);

        if (isVerified) {
            return new ResponseEntity<>("User verified successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid OTP or OTP expired", HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/greet")
    public String greet(@RequestParam(value = "name", defaultValue = "world") String name) {
        return "HEllO " + name;
    }


    @GetMapping("/greet/{email}")
    public String greetbyData(@PathVariable(value = "email") String email) {
        try {
            Users user = userService.getUserbyemail(email).orElseThrow(
                    () -> new UserPrincipalNotFoundException("User not found by " + email));
            return "HEllO " + user.getName();
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    // Get all events
    @GetMapping("/getAll")
    public List<Users> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get event by id
    @GetMapping("/{id}")
    public ResponseEntity<Users> getEventById(@PathVariable String id) throws
            UserPrincipalNotFoundException {
        Users user = userService.getUserbyemail(id).orElseThrow(
                () -> new UserPrincipalNotFoundException("User not found by ID" + id));

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/update/{email}")
    public ResponseEntity<?> updateEvent
            (@PathVariable Long id, @RequestBody UserCreationDTO request) {

        try {
            Users user = userService.getUserbyemail(request.getEmail()).orElseThrow(
                    () -> new UserPrincipalNotFoundException("User not found by Email " + request.getEmail()));

            Users updatedUser = userService.updateUser(user, request);

            if (updatedUser == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete event by id
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteEvent(@PathVariable String email) {
        try {

            userService.deleteUser(email);
            return ResponseEntity.ok("User Deleted");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<String> getUsernameFromToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = JwtUtils.getUsernameFromJwtToken(jwtToken);
            return new ResponseEntity<>(username, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCreationDTO loginDTO) {

        Users user = userRepository.findByEmail(loginDTO.getEmail()).orElse(null);

        if (user != null && passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            String jwtToken = JwtUtils.generateJwtToken(user.getEmail());
            return ResponseEntity.ok(jwtToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


}
