package hw.oauth2.entities;

import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, String> {

    User findOne(String userId);
}
