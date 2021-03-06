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
package oauth2.authentication.tokens

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource

import oauth2.entities.AccessToken
import oauth2.entities.AccessTokenRepository
import oauth2.entities.RefreshTokenRepository

import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = TestConfiguration)
@ActiveProfiles("dev")
@TestPropertySource(properties = "logging.level.org=ERROR")
class TokenStoreImplTest extends TokenStoreBaseTests {

    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = AccessToken)
    @EnableJpaRepositories(basePackageClasses = AccessTokenRepository)
    static class TestConfiguration {

        @Bean
        @Primary
        DataSource getDataSource() {
            return new EmbeddedDatabaseBuilder().build()
        }
    }

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    PlatformTransactionManager transactionManager

    @Autowired
    AccessTokenRepository accessTokenRepository

    @Autowired
    RefreshTokenRepository refreshTokenRepository

    TokenServiceImpl tokenStore

    @Before
    void setup() {
        tokenStore = new TokenServiceImpl(accessTokenRepository, refreshTokenRepository)
    }

    @After
    @Transactional
    void cleanup() {
        new TransactionTemplate(transactionManager).execute { status ->
            entityManager.createQuery("delete from AccessToken").executeUpdate()
            entityManager.createQuery("delete from RefreshToken").executeUpdate()
        }
    }

    @Override
    TokenServiceImpl getTokenStore() {
        return tokenStore
    }
}
