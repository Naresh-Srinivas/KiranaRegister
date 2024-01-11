package example.kirana.security;

import example.kirana.ratelimit.RateLimitConfig;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom JWT authentication filter that integrates rate limiting.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthService jwtAuthService;
    private final RateLimitConfig rateLimitConfig;

    /**
     * Constructor for JwtAuthenticationFilter.
     *
     * @param jwtAuthService        Service for JWT authentication operations.
     * @param customUserDetailsService Custom user details service.
     * @param rateLimitConfig       Rate limit configuration.
     */
    public JwtAuthenticationFilter(JwtAuthService jwtAuthService, CustomUserDetailsService customUserDetailsService, RateLimitConfig rateLimitConfig) {
        this.jwtAuthService = jwtAuthService;
        this.customUserDetailsService = customUserDetailsService;
        this.rateLimitConfig = rateLimitConfig;
    }

    /**
     * Internal method to perform JWT authentication and rate limiting for each request.
     *
     * @param request     HTTP request.
     * @param response    HTTP response.
     * @param filterChain Filter chain for processing the request.
     * @throws ServletException If an exception occurs during the filter chain execution.
     * @throws IOException      If an I/O exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract token and username from the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtAuthService.extractUsername(token);
        }

        // Check if the user is authenticated and if the token is valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Validate the token and perform rate limiting
            if (!jwtAuthService.validateToken(token, userDetails)) {
                Bucket bucket = rateLimitConfig.resolveBucket("SER");
                if (bucket.tryConsume(1)) {
                    // If rate limit allows, authenticate the user
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // If rate limit is exceeded, return 429 (Too Many Requests) status
                    response.setStatus(429);
                    return;
                }
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
