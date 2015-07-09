package hw.oauth2.clients;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientDetailsService {

    private static final String SQL_LOAD_CLIENTS_BY_CLIENTID = "select " //
            + "    client_id, " //
            + "    client_secret, " //
            + "    access_token_validity, " //
            + "    refresh_token_validity " //
            + "from " //
            + "    t_clients " //
            + "where " //
            + "    client_id = ?";

    private static final String SQL_LOAD_ENTRIES_BY_CLIENTID = "select " //
            + "    name, " //
            + "    data " //
            + "from " //
            + "    t_client_entries " //
            + "where " //
            + "    client_id = ?";

    private enum EntryType {
        GRANT_TYPE {

            @Override
            void setValue(String value, ClientDetailsBuilder user) {
                user.withAuthorizedGrantTypes(value);
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
                user.withAuthorizedGrantTypes(value);
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

    private final JdbcTemplate jdbcTemplate;

    public ClientServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        List<ClientDetailsBuilder> clients = loadClientsByClientId(clientId);
        if (clients.isEmpty()) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        ClientDetailsBuilder client = clients.get(0);
        loadEntries(client.clientId(), client);
        return client.build();
    }

    private List<ClientDetailsBuilder> loadClientsByClientId(String clientId) {
        return jdbcTemplate.query(SQL_LOAD_CLIENTS_BY_CLIENTID, new String[] { clientId.toLowerCase() },
                (rs, rowNum) -> {
                    return new ClientDetailsBuilder() //
                            .clientId(rs.getString(1)) //
                            .clientSecret(rs.getString(2)) //
                            .accessTokenValiditySeconds(rs.getInt(3)) //
                            .refreshTokenValiditySeconds(rs.getInt(4));
                });
    }

    private void loadEntries(String clientId, ClientDetailsBuilder client) {
        jdbcTemplate.query(SQL_LOAD_ENTRIES_BY_CLIENTID, new String[] { clientId }, rs -> {
            String name = rs.getString(1);
            String data = rs.getString(2);
            findEntry(name).setValue(data, client);
        });
    }

    private EntryType findEntry(String name) {
        try {
            return EntryType.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return EntryType.IGNORE;
        }
    }
}
