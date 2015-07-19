package hw.oauth2.authentication.tokens

import hw.oauth2.entities.AccessToken
import hw.oauth2.entities.AccessTokenRepository
import hw.oauth2.entities.RefreshTokenRepository

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = TestConfiguration)
@ActiveProfiles("dev")
class TokenStoreImplTest extends TokenServiceImplTest {

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = AccessToken)
    @EnableJpaRepositories(basePackageClasses = AccessTokenRepository)
    static class TestConfiguration {
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

    //    @Before
    //    void setUp() throws Exception {
    //        // creates a HSQL in-memory db populated from default scripts classpath:schema.sql and classpath:data.sql
    //        db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()
    //
    //        AccessTokenRepository repo1 = new Acc
    //        tokenStore = new TokenServiceImpl(db)
    //    }
    //
    //    @Test
    //    void testFindAccessTokensByUserName() {
    //        OAuth2Authentication expectedAuthentication = new OAuth2Authentication(RequestTokenFactory.createOAuth2Request("id", false),
    //                new TokenServiceImplTest.TestAuthentication("test2", false))
    //        OAuth2AccessToken expectedOAuth2AccessToken = new DefaultOAuth2AccessToken("testToken")
    //        getTokenStore().storeAccessToken(expectedOAuth2AccessToken, expectedAuthentication)
    //
    //        Collection<OAuth2AccessToken> actualOAuth2AccessTokens = getTokenStore().findTokensByUserName("test2")
    //        assert actualOAuth2AccessTokens.size()  == 1
    //    }
    //
    //    @After
    //    public void tearDown() throws Exception {
    //        db.shutdown()
    //    }
}
