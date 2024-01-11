package example.kirana.repositories;

import example.kirana.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsersRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    // Add custom queries if needed
}

