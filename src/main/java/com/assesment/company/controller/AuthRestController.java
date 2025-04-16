package com.assesment.company.controller;

import com.assesment.company.entity.User;
import com.assesment.company.security.JwtTokenUtil;
import com.assesment.company.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthRestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            logger.info("Attempting to register user with email: {}", user.getEmail());
            
            // Set default values for new user
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            
            // Set default role if not provided
            if (user.getRole() == null) {
                user.setRole(User.UserRole.COMPANY); // Default role
            }
            
            User registeredUser = userService.registerUser(user);
            logger.info("Successfully registered user with email: {}", user.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "user", registeredUser
            ));
        } catch (Exception e) {
            logger.error("Registration failed for email: {}. Error: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Registration failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            logger.info("Login attempt for email: {}", email);

            if (email == null || password == null) {
                logger.error("Login failed: Email or password is missing");
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", "Email and password are required"
                ));
            }

            try {
                // First, verify if the user exists
                User user = userService.getUserByEmail(email);
                if (!user.isEnabled()) {
                    logger.error("Login failed: Account is disabled for email: {}", email);
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Login failed",
                        "message", "Account is disabled"
                    ));
                }

                // Attempt authentication
                userService.authenticate(email, password);
                logger.info("Authentication successful for email: {}", email);

                // Generate token
                final UserDetails userDetails = userService.loadUserByUsername(email);
                final String token = jwtTokenUtil.generateToken(userDetails);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()
                ));

                logger.info("Login successful for email: {}", email);
                return ResponseEntity.ok(response);

            } catch (UsernameNotFoundException e) {
                logger.error("Login failed: User not found with email: {}", email);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Login failed",
                    "message", "User not found"
                ));
            } catch (BadCredentialsException e) {
                logger.error("Login failed: Invalid credentials for email: {}", email);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Login failed",
                    "message", "Invalid email or password"
                ));
            }
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Login failed",
                "message", "An unexpected error occurred"
            ));
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok(Map.of(
            "message", "Please use POST method to login",
            "required_fields", Map.of(
                "email", "string",
                "password", "string"
            )
        ));
    }

    @PostMapping("/enable")
    public ResponseEntity<?> enableUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", "Email is required"
                ));
            }

            logger.info("Attempting to enable user account for email: {}", email);
            userService.enableExistingUser(email);
            logger.info("Successfully enabled user account for email: {}", email);
            
            return ResponseEntity.ok(Map.of(
                "message", "User account enabled successfully"
            ));
        } catch (Exception e) {
            logger.error("Failed to enable user account. Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to enable user account",
                "message", e.getMessage()
            ));
        }
    }
} 