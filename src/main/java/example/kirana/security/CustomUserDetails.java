package example.kirana.security;

import example.kirana.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Custom implementation of UserDetails for representing user details in the Kirana application.
 */
public class CustomUserDetails implements UserDetails {

    private String name;
    private String password;
    private List<GrantedAuthority> authorities;

    /**
     * Constructor to create CustomUserDetails from User entity.
     *
     * @param userInfo User entity containing user details.
     */
    public CustomUserDetails(User userInfo) {
        this.name = userInfo.getName();
        this.password = userInfo.getPassword();
        // Assuming the role is a single authority, you may modify as needed
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(userInfo.getRole()));
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of authorities granted to the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The password.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return The username.
     */
    @Override
    public String getUsername() {
        return name;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return True if the user's account is valid (i.e., not expired), false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Account expiration logic can be added if needed
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return True if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Account locking logic can be added if needed
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return True if the user's credentials are valid (i.e., not expired), false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credential expiration logic can be added if needed
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return True if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true; // User enable/disable logic can be added if needed
    }
}
