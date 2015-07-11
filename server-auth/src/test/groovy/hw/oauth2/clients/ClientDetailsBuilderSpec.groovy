package hw.oauth2.clients

import org.springframework.security.oauth2.provider.ClientDetails

import spock.lang.Specification

class ClientDetailsBuilderSpec extends Specification {

    def "set client id"() {
        given:
        String clientId = "a client id"

        when:
        ClientDetails client = clientDetailsBuilder().clientId(clientId).build()

        then:
        client.clientId == clientId
    }

    def "set client secret"() {
        given:
        String clientSecret = "client secret"

        when:
        ClientDetails client = clientDetailsBuilder().clientSecret(clientSecret).build()

        then:
        client.clientSecret == clientSecret
    }

    def "set grant types"() {
        given:
        String grantType1 = "grant type ab"
        String grantType2 = "grant type db"

        when:
        ClientDetails client = clientDetailsBuilder() //
                .withAuthorizedGrantType(grantType1).withAuthorizedGrantType(grantType2).build()

        then:
        client.authorizedGrantTypes == [grantType1, grantType2] as Set
    }

    def "set redirect uris"() {
        given:
        String redirectUri1 = "redirect uri 1"
        String redirectUri2 = "redirect uri 2"

        when:
        ClientDetails client = clientDetailsBuilder().withRedirectUri(redirectUri1).withRedirectUri(redirectUri2).build()

        then:
        client.registeredRedirectUri == [redirectUri1, redirectUri2] as Set
    }

    def "set scopes"() {
        given:
        String scope1 = "scope fgfh"
        String scope2 = "scope fhfhdh"

        when:
        ClientDetails client = clientDetailsBuilder() //
                .withScope(scope1).withScope(scope2).build()

        then:
        client.scope == [scope1, scope2] as Set
    }

    def "set auto approved scopes"() {
        given:
        String approvedScope1 = "auto approved scope fgfh"
        String approvedScope2 = "auto approved scope fhfhdh"

        when:
        ClientDetails client = clientDetailsBuilder() //
                .withAutoApprovedScope(approvedScope1).withAutoApprovedScope(approvedScope2).build()

        then:
        client.isAutoApprove(approvedScope1)
        client.isAutoApprove(approvedScope2)
    }

    def "an auto approved scope is also a scope"() {
        given:
        String approvedScope = "auto approved scope fgfh"

        when:
        ClientDetails client = clientDetailsBuilder().withAutoApprovedScope(approvedScope).build()

        then:
        client.scope.contains(approvedScope)
    }

    def "a 'no auto approved' scope is not auto approved"() {
        given:
        String scope = "no auto approved scope"

        when:
        ClientDetails client = clientDetailsBuilder().withScope(scope).build()

        then:
        !client.isAutoApprove(scope)
    }

    def "set authorities"() {
        given:
        String authority1 = "authority 1"
        String authority2 = "authority 2"

        when:
        ClientDetails client = clientDetailsBuilder().withAuthority(authority1).withAuthority(authority2).build()

        then:
        client.authorities.authority as Set == [authority1, authority2] as Set
    }

    def "set roles"() {
        given:
        String role1 = "role 1"
        String role2 = "role 2"

        when:
        ClientDetails client = clientDetailsBuilder().withRole(role1).withRole(role2).build()

        then:
        client.authorities.authority as Set == [
            "ROLE_" + role1,
            "ROLE_" + role2] as Set
    }

    def "set resource ids"() {
        given:
        String resourceId1 = "resource id 1"
        String resourceId2 = "resource id 2"

        when:
        ClientDetails client = clientDetailsBuilder().withResourceId(resourceId1).withResourceId(resourceId2).build()

        then:
        client.resourceIds == [resourceId1, resourceId2] as Set
    }

    def "set access token validity seconds"() {
        given:
        int accessTokenValiditySeconds = 23

        when:
        ClientDetails client = clientDetailsBuilder().accessTokenValiditySeconds(accessTokenValiditySeconds).build()

        then:
        client.accessTokenValiditySeconds == accessTokenValiditySeconds
    }

    def "set access token validity seconds to 0"() {
        given:
        int accessTokenValiditySeconds = 0

        when:
        ClientDetails client = clientDetailsBuilder().accessTokenValiditySeconds(accessTokenValiditySeconds).build()

        then:
        client.accessTokenValiditySeconds == null
    }

    def "set refresh token validity seconds"() {
        given:
        int refreshTokenValiditySeconds = 23

        when:
        ClientDetails client = clientDetailsBuilder().refreshTokenValiditySeconds(refreshTokenValiditySeconds).build()

        then:
        client.refreshTokenValiditySeconds == refreshTokenValiditySeconds
    }

    def "set refresh token validity seconds to 0"() {
        given:
        int refreshTokenValiditySeconds = 0

        when:
        ClientDetails client = clientDetailsBuilder().refreshTokenValiditySeconds(refreshTokenValiditySeconds).build()

        then:
        client.refreshTokenValiditySeconds == null
    }

    private ClientDetailsBuilder clientDetailsBuilder() {
        return new ClientDetailsBuilder().clientId("fgfg35")
    }
}
