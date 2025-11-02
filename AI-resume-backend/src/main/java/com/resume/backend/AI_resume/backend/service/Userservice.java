package com.resume.backend.AI_resume.backend.service;

import com.resume.backend.AI_resume.backend.model.SignIn;
import com.resume.backend.AI_resume.backend.model.User;
import com.resume.backend.AI_resume.backend.model.UserRequest;
import com.resume.backend.AI_resume.backend.model.UserResponse;
import com.resume.backend.AI_resume.backend.repo.UsersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Userservice {

    private static final Logger logger = LoggerFactory.getLogger(Userservice.class);
    private final UsersRepo usersRepo;

    public Userservice(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    @Transactional
    public UserResponse register(UserRequest request) {
        logger.info("Registering user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (usersRepo.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create a new user entity
        // Note: createdAt and updatedAt are automatically set by @PrePersist
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword()) // you can hash it later
                .build();

        // Save to MySQL
        User savedUser = usersRepo.save(user);
        logger.info("User saved with ID: {}", savedUser.getId());

        // Prepare response
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());

        return response;
    }

    public String signIn(SignIn request) {
        logger.info("Attempting sign in for email: {}", request.getEmail());
        
        // Find user by email
        User user = usersRepo.findByEmail(request.getEmail())
                .orElse(null);

        // If user not found or password doesn't match
        if (user == null) {
            logger.warn("Sign in failed: User not found for email: {}", request.getEmail());
            return "Invalid email or password";
        }
        
        if (!user.getPassword().equals(request.getPassword())) {
            logger.warn("Sign in failed: Invalid password for email: {}", request.getEmail());
            return "Invalid email or password";
        }

        // Successful login — return user's name
        logger.info("Sign in successful for user: {}", user.getName());
        return user.getName();
    }
}
