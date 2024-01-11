package example.kirana.security;

import example.kirana.models.User;
import example.kirana.repositories.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public CustomUserDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user details by username for authentication.
     *
     * @param username The username for which to load user details.
     * @return UserDetails for the given username.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Authentication entry point 2 called");

        // Find user details by username from the repository
        Optional<User> userInfo = userRepository.findByUsername(username);

        // Map User entity to CustomUserDetails or throw exception if user not found
        return userInfo.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
