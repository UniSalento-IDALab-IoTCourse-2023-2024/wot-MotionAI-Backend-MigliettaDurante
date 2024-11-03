package it.unisalento.recproject.authenticationservice.repositories;

import it.unisalento.recproject.authenticationservice.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
