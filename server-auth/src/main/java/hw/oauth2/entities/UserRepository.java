package hw.oauth2.entities;

import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, String> {

    User findByUserId(String userId);

    void save(User user);

    void delete(User user);
}
