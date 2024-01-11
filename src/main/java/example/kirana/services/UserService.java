package example.kirana.services;

import example.kirana.models.User;
import example.kirana.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users.
     *
     * @return List of all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Create a new user.
     *
     * @param user The user to be created.
     * @return The created user.
     */
    public User createUser(User user) {
        // Implement validation or business logic if needed
        // Encrypt the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Update an existing user by ID.
     *
     * @param userId       The ID of the user to be updated.
     * @param updatedUser  The updated user data.
     * @return The updated user.
     */
    public User updateUser(String userId, User updatedUser) {
        // Implement validation or business logic if needed
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser != null) {
            // Update user properties
            existingUser.setName(updatedUser.getName());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRole(updatedUser.getRole());

            return userRepository.save(existingUser);
        }
        return null; // Or throw an exception indicating user not found
    }

    /**
     * Delete a user by ID.
     *
     * @param userId The ID of the user to be deleted.
     */
    public void deleteUser(String userId) {
        // Implement validation or business logic if needed
        userRepository.deleteById(userId);
    }
}
