package com.resume.backend.AI_resume.backend.controller;

import com.resume.backend.AI_resume.backend.model.SignIn;
import com.resume.backend.AI_resume.backend.model.UserRequest;
import com.resume.backend.AI_resume.backend.model.UserResponse;
import com.resume.backend.AI_resume.backend.service.Userservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Userservice userservice;

    public UserController(Userservice userservice) {
        this.userservice = userservice;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request){
        try {
            logger.info("Register request received for email: {}", request.getEmail());
            
            // Validate request
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Name is required"));
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email is required"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Password is required"));
            }
            
            UserResponse response = userservice.register(request);
            logger.info("User registered successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during registration: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignIn request) {
        try {
            logger.info("Sign in request received for email: {}", request.getEmail());
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Email is required"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Password is required"));
            }
            
            String name = userservice.signIn(request);
            
            if (name == null || "Invalid email or password".equals(name)) {
                logger.warn("Invalid login attempt for email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Invalid email or password"));
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("name", name);
            logger.info("User signed in successfully: {}", name);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during sign in: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Sign in failed: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        return error;
    }
}
