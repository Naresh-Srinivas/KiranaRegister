package example.kirana.security;

import example.kirana.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for securing the Kirana application.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final UsersRepository userRepository;

    /**
     * Configures the user details service for authentication.
     *
     * @return UserDetailsService implementation.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain for HTTP security settings.
     *
     * @param http HttpSecurity instance for configuring security settings.
     * @return SecurityFilterChain configured with authentication and authorization rules.
     * @throws Exception If an exception occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests()
                .requestMatchers("/kirana/authenticate").permitAll()
                .requestMatchers("/kirana/**").hasAuthority("USER")
                .requestMatchers("/reports/monthly").hasAuthority("USER")
                .requestMatchers("/transactions").hasAuthority("USER")
                .requestMatchers("/transactions/employee").hasAuthority("EMPLOYEE")
                .requestMatchers("/transactions/admin").hasAuthority("USER")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures the password encoder for secure password storage.
     *
     * @return PasswordEncoder implementation.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider with user details service and password encoder.
     *
     * @return AuthenticationProvider implementation.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Configures the authentication manager for the application.
     *
     * @param config AuthenticationConfiguration instance.
     * @return AuthenticationManager implementation.
     * @throws Exception If an exception occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
