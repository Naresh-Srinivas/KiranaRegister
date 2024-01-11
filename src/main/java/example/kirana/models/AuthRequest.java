package example.kirana.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class AuthRequest {

    @Id
    private String username;
    private String password;

    // Getters and setters
}
