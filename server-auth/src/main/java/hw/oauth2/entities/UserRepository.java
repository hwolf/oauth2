package hw.oauth2.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, String> {

    User findOne(String userId);

    Page<User> findAll(Pageable page);

    User findByUserId(String userId);

    User save(User user);

    void delete(User user);
}
