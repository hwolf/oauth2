package hw.oauth2.tokens;

class TokenServiceSqls {

    static final String SQL_SELECT_ACCESS_TOKEN = "select " //
            + "    token_id, " //
            + "    token " //
            + "from " //
            + "    t_access_tokens " //
            + "where " //
            + "    token_id = ?";

    static final String SQL_SELECT_ACCESS_TOKEN_AUTHENTICATION = "select " //
            + "    token_id, " //
            + "    authentication " //
            + "from " //
            + "    t_access_tokens " //
            + "where " //
            + "    token_id = ?";

    static final String SQL_SELECT_ACCESS_TOKEN_FROM_AUTHENTICATION = "select " //
            + "    token_id, " //
            + "    token " //
            + "from " //
            + "    t_access_tokens " //
            + "where " //
            + "    authentication_id = ?";

    static final String SQL_SELECT_DEFAULT_TOKENS_FROM_USERNAME = "select " //
            + "    token_id, " //
            + "    token " //
            + "from " //
            + "    t_access_tokens " //
            + "where " //
            + "    user_name = ?";

    static final String SQL_SELECT_DEFAULT_TOKENS_FROM_CLIENT = "select " //
            + "    token_id, " //
            + "    token " //
            + "from " //
            + "    t_access_tokens " //
            + "where " //
            + "    client_id = ?";

    static final String SQL_SELECT_DEFAULT_TOKENS_FROM_USERNAME_AND_CLIENT = "select " //
            + "    token_id, " //
            + "    token " //
            + "from " //
            + "    t_access_tokens " //
            + "where " //
            + "    user_id = ? and " //
            + "    client_id = ?";

    static final String SQL_INSERT_ACCESS_TOKEN = "insert into " //
            + "    t_access_tokens " //
            + "    (token_id, token, authentication_id, user_id, client_id, authentication, refresh_token) " + "values " //
            + "    (?, ?, ?, ?, ?, ?, ?)";

    static final String SQL_DELETE_ACCESS_TOKEN = "delete from " //
            + "    t_access_tokens " //
            + "where " //
            + "    token_id = ?";

    static final String SQL_DELETE_FROM_REFRESH_TOKEN = "delete from " //
            + "    t_access_tokens " //
            + "where " //
            + "    refresh_token = ?";

    // ------------------------------------------------------

    static final String SQL_SELECT_REFRESH_TOKENT = "select " //
            + "    token_id, " //
            + "    token " //
            + "from " //
            + "    t_refresh_tokens " //
            + "where " //
            + "    token_id = ?";

    static final String SQL_SELECT_REFRESH_TOKEN_AUTHENTICATION = "select " //
            + "    token_id, " //
            + "    authentication " //
            + "from " //
            + "    t_refresh_tokens " //
            + "where " //
            + "    token_id = ?";

    static final String SQL_INSERT_REFRESH_TOKEN = "insert into " //
            + "t_refresh_tokens (token_id, token, authentication) values (?, ?, ?)";

    static final String SQL_DELETE_REFRESH_TOKEN = "delete from " //
            + "    t_refresh_tokens where token_id = ?";
}
