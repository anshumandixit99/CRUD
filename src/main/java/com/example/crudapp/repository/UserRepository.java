
package com.example.crudapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.crudapp.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    // Custom query methods can be added here
    Optional<Users> findByEmail(String email);
    Optional<Users> findByjwtToken(String token);

}
