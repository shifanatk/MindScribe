package com.mindscribe.repository.mysql;

import com.mindscribe.model.mysql.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB-backed repository for users (Atlas).
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
}
