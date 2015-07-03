package hw.oauth2.authentication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ClientServiceImpl implements ClientDetailsService {

    private static final String SQL_LOAD_CLIENTS_BY_CLIENT_ID = "select " //
            + "    client_id, " //
            + "    client_secret, " //
            + "    web_server_redirect_uri, " //
            + "    access_token_validity, " //
            + "    refresh_token_validity " //
            + "from " //
            + "    t_clients " //
            + "where " //
            + "    client_id = ?";

    private static final String SQL_LOAD_AUTHORIZATION_TYPES = "select " //
            + "    authorized_grant_type " //
            + "from " //
            + "    t_client_authorized_grant_types " //
            + "where " //
            + "    client_id = ?";

    private static final String SQL_LOAD_AUTHORITIES = "select " //
            + "    authority " //
            + "from " //
            + "    t_client_authorities " //
            + "where " //
            + "    client_id = ?";

    private static final String SQL_LOAD_RESOURCE_IDS = "select " //
            + "    resource_id " //
            + "from " //
            + "    t_client_resources " //
            + "where " //
            + "    client_id = ?";

    private static final String SQL_LOAD_SCOPES = "select " //
            + "    scope,approved " //
            + "from " //
            + "    t_client_scopes " //
            + "where " //
            + "    client_id = ?";

    private final JdbcTemplate jdbc;

    public ClientServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        List<ClientDetailsBuilder> clients = loadClientsByClientId(clientId);
        if (clients.isEmpty()) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        ClientDetailsBuilder client = clients.get(0);
        loadAuthorizedGrantTypes(client.clientId(), client);
        loadAuthorities(client.clientId(), client);
        loadResourceIds(client.clientId(), client);
        loadScope(clientId, client);
        return client.build();
    }

    private List<ClientDetailsBuilder> loadClientsByClientId(String clientId) {
        return jdbc.query(SQL_LOAD_CLIENTS_BY_CLIENT_ID, new String[] { clientId.toLowerCase() }, (rs, rowNum) -> {
            return new ClientDetailsBuilder() //
                    .clientId(rs.getString(1)) //
                    .clientSecret(rs.getString(2)) //
                    .redirectUri(rs.getString(3)) //
                    .accessTokenValiditySeconds(mapInteger(rs, 4)) //
                    .refreshTokenValiditySeconds(mapInteger(rs, 5));
        });
    }

    private void loadAuthorizedGrantTypes(String clientId, ClientDetailsBuilder client) {
        jdbc.query(SQL_LOAD_AUTHORIZATION_TYPES, new String[] { clientId }, rs -> {
            client.authorizedGrantTypes(rs.getString(1));
        });
    }

    private void loadAuthorities(String clientId, ClientDetailsBuilder client) {
        jdbc.query(SQL_LOAD_AUTHORITIES, new String[] { clientId }, rs -> {
            client.authorities(new SimpleGrantedAuthority(rs.getString(1)));
        });
    }

    private void loadResourceIds(String clientId, ClientDetailsBuilder client) {
        jdbc.query(SQL_LOAD_RESOURCE_IDS, new String[] { clientId }, rs -> {
            client.resourceIds(rs.getString(1));
        });
    }

    private void loadScope(String clientId, ClientDetailsBuilder client) {
        jdbc.query(SQL_LOAD_SCOPES, new String[] { clientId }, rs -> {
            String scope = rs.getString(1);
            boolean approved = rs.getBoolean(2);
            client.scopes(scope);
            if (approved) {
                client.approvedScopes(scope);
            }
        });
    }

    private static Integer mapInteger(ResultSet rs, int columnIndex) throws SQLException {
        if (rs.getObject(columnIndex) == null) {
            return null;
        }
        return rs.getInt(columnIndex);
    }
}
