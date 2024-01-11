package example.kirana.controllers;

import example.kirana.models.User;
import example.kirana.security.SecurityConfig;
import example.kirana.services.UserService;
import example.kirana.models.AuthRequest;
import example.kirana.security.JwtAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for handling user-related operations and authentication in the Kirana application.
 */
@RestController
@RequestMapping("/kirana")
public class UserController {

    private final UserService userService;
    private final JwtAuthService jwtAuthService;

    private final AuthenticationManager authenticationManager;

    /**
     * Constructs a new UserController with the specified services and authentication manager.
     *
     * @param authenticationManager The authentication manager for user authentication.
     * @param jwtAuthService        The service for JWT-related operations.
     * @param userService           The service responsible for managing users.
     */
    @Autowired
    public UserController(AuthenticationManager authenticationManager, JwtAuthService jwtAuthService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
        this.userService = userService;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return List of all users.
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Creates a new user.
     *
     * @param user The user details to create.
     * @return The created user.
     */
    @PostMapping("/new")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Updates an existing user.
     *
     * @param userId The ID of the user to update.
     * @param user   The updated user details.
     * @return The updated user.
     */
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param authRequest The authentication request containing username and password.
     * @return ResponseEntity containing the JWT token.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        logger.info("Authentication entry point 1 called");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtAuthService.generateToken(authRequest.getUsername());
                Map<String, String> response = Collections.singletonMap("token", token);
                return ResponseEntity.ok(response);
            } else {
                throw new BadCredentialsException("Invalid user credentials");
            }
        } catch (Exception e) {
            logger.error("Authentication failed", e);
            throw new BadCredentialsException("Authentication failed", e);
        }
    }
}
