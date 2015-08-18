package oauth2.authentication.clients

import oauth2.entities.Client
import oauth2.entities.ClientRepository
import oauth2.entities.Entry

import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.NoSuchClientException

import spock.lang.Specification


class ClientServiceImplSpec extends Specification {

    ClientRepository clientRepository = Mock()
    ClientServiceImpl clientService = new ClientServiceImpl(clientRepository)

    def "if client is not found, a NoSuchClientException exception will be thrown"() {

        given:
        String clientId = "a client id"

        when:
        clientRepository.findByClientId(clientId) >> null
        clientService.loadClientByClientId(clientId)

        then:
        thrown(NoSuchClientException)
    }

    def "if client is found, a ClientDetails with the given client id will be returned "() {

        given:
        String clientId = "a client id"

        when:
        clientRepository.findByClientId(clientId) >> new Client(clientId: clientId)
        ClientDetails client = clientService.loadClientByClientId(clientId)

        then:
        client.clientId == clientId
    }

    def "Grant types should be mapped"() {

        given:
        Set<String> grantTypes = ["grant type 1", "grant type 2"]
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        grantTypes.each { grantType ->
            clientService.mapEntry(builder).accept(Entry.create("GRANT_TYPE", grantType))
        }

        then:
        builder.build().authorizedGrantTypes == grantTypes
    }

    def "Roles should be mapped"() {

        given:
        String role = "role 1"
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        clientService.mapEntry(builder).accept(Entry.create("ROLE", role))

        then:
        builder.build().authorities.authority.contains("ROLE_" + role)
    }

    def "Authorities should be mapped"() {

        given:
        String authority = "authority 1"
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        clientService.mapEntry(builder).accept(Entry.create("AUTHORITY", authority))

        then:
        builder.build().authorities.authority.contains(authority)
    }

    def "Resource ids should be mapped"() {

        given:
        Set<String> resourceIds = ["resource id 1", "resource id 2"]
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        resourceIds.each { id ->
            clientService.mapEntry(builder).accept(Entry.create("RESOURCE_ID", id))
        }

        then:
        builder.build().resourceIds == resourceIds
    }

    def "Scopes should be mapped"() {

        given:
        Set<String> scopes = ["scope 1", "scope 2"]
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        scopes.each { scope ->
            clientService.mapEntry(builder).accept(Entry.create("SCOPE", scope))
        }

        then:
        builder.build().scope == scopes
    }

    def "Approved scopes should be mapped"() {

        given:
        String scope = "scope 1"
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        clientService.mapEntry(builder).accept(Entry.create("APPROVED_SCOPE", scope))

        then:
        builder.build().isAutoApprove(scope)
    }

    def "Redirect URIs should be mapped"() {

        given:
        Set<String> redirectUris = ["redirect uri 1", "redirect uri 2"]
        ClientDetailsBuilder builder = new ClientDetailsBuilder()

        when:
        redirectUris.each { uri ->
            clientService.mapEntry(builder).accept(Entry.create("REDIRECT_URI", uri))
        }

        then:
        builder.build().registeredRedirectUri == redirectUris
    }
}
