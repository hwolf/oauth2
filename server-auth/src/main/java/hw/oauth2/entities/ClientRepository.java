package hw.oauth2.entities;

import org.springframework.data.repository.Repository;

public interface ClientRepository extends Repository<Client, String> {

    Client findByClientId(String clientId);
}
