/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.authentication.clients;

import java.util.function.Consumer;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;

import oauth2.entities.Client;
import oauth2.entities.ClientRepository;
import oauth2.entities.Entry;

@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientDetailsService {

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
                user.withAutoApprovedScope(value);
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

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        Client client = clientRepository.findByClientId(clientId);
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
    }

    @VisibleForTesting
    Consumer<Entry> mapEntry(ClientDetailsBuilder builder) {
        return entry -> findMapper(entry.getName()).setValue(entry.getData(), builder);
    }

    private EntryMapper findMapper(String name) {
        for (EntryMapper mapper : EntryMapper.values()) {
            if (Objects.equal(name, mapper.name())) {
                return mapper;
            }
        }
        return EntryMapper.IGNORE;
    }
}
