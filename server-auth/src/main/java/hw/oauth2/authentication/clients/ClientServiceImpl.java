package hw.oauth2.authentication.clients;

import java.util.function.Consumer;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.transaction.annotation.Transactional;

import hw.oauth2.entities.Client;
import hw.oauth2.entities.ClientRepository;
import hw.oauth2.entities.Entry;

@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientDetailsService {

    // private static final String SQL_LOAD_CLIENTS_BY_CLIENTID = "select " //
    // + " client_id, " //
    // + " client_secret, " //
    // + " access_token_validity, " //
    // + " refresh_token_validity " //
    // + "from " //
    // + " t_clients " //
    // + "where " //
    // + " client_id = ?";
    //
    // private static final String SQL_LOAD_ENTRIES_BY_CLIENTID = "select " //
    // + " name, " //
    // + " data " //
    // + "from " //
    // + " t_client_entries " //
    // + "where " //
    // + " client_id = ?";

    private enum EntryMapper {
        GRANT_TYPE {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withAuthorizedGrantType(value);
            }
        },

        ROLE {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withRole(value);
            }
        },

        AUTHORITY {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withAuthority(value);
            }
        },

        RESOURCE_ID {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withResourceId(value);
            }
        },

        SCOPE {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withScope(value);
            }
        },

        APPROVED_SCOPE {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withAuthorizedGrantType(value);
            }
        },

        REDIRECT_URI {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withRedirectUri(value);
            }
        },

        IGNORE {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                // Ignore call
            }
        };

        abstract void setValue(String value, ClientDetailsBuilder user);
    }

    // private final JdbcTemplate jdbcTemplate;
    //
    // public ClientServiceImpl(JdbcTemplate jdbcTemplate) {
    // this.jdbcTemplate = jdbcTemplate;
    // }

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client client = clientRepository.findOne(clientId.toLowerCase());
        if (client == null) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        ClientDetailsBuilder builder = new ClientDetailsBuilder() //
                .clientId(client.getClientId()) //
                .clientSecret(client.getClientSecret()) //
                .accessTokenValiditySeconds(client.getAccessTokenValidity()) //
                .refreshTokenValiditySeconds(client.getRefreshTokenValidity());
        client.getEntries().stream().forEach(mapEntry(builder));
        return builder.build();
        // List<ClientDetailsBuilder> clients = loadClientsByClientId(clientId);
        // if (clients.isEmpty()) {
        // throw new NoSuchClientException("No client with requested id: " + clientId);
        // }
        // ClientDetailsBuilder client = clients.get(0);
        // loadEntries(client.clientId(), client);
        // return client.build();
    }

    // private List<ClientDetailsBuilder> loadClientsByClientId(String clientId) {
    // return jdbcTemplate.query(SQL_LOAD_CLIENTS_BY_CLIENTID, new String[] { clientId.toLowerCase()
    // },
    // (rs, rowNum) -> {
    // return new ClientDetailsBuilder() //
    // .clientId(rs.getString(1)) //
    // .clientSecret(rs.getString(2)) //
    // .accessTokenValiditySeconds(rs.getInt(3)) //
    // .refreshTokenValiditySeconds(rs.getInt(4));
    // });
    // }
    //
    // private void loadEntries(String clientId, ClientDetailsBuilder client) {
    // jdbcTemplate.query(SQL_LOAD_ENTRIES_BY_CLIENTID, new String[] { clientId }, rs -> {
    // String name = rs.getString(1);
    // String data = rs.getString(2);
    // findEntry(name).setValue(data, client);
    // });
    // }

    private Consumer<? super Entry> mapEntry(ClientDetailsBuilder builder) {
        return entry -> findMapper(entry.getName()).setValue(entry.getData(), builder);
    }

    private EntryMapper findMapper(String name) {
        try {
            return EntryMapper.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return EntryMapper.IGNORE;
        }
    }
}
