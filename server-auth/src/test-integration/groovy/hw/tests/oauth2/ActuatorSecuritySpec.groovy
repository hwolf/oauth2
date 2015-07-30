package hw.tests.oauth2

import hw.oauth2.entities.UserRepository
import hw.tests.oauth2.pages.LoginPage
import hw.tests.oauth2.utils.UserHelper
import spock.lang.Unroll

class ActuatorSecuritySpec extends HwOauth2Spec {

    static final Collection<String> ENDPOINTS = [
        'manage/autoconfig',
        'manage/beans',
        'manage/configprops',
        'manage/dump',
        'manage/env',
        'manage/health',
        'manage/info',
        'manage/metrics',
        'manage/mappings',
        'manage/trace',
        'manage/jolokia'
    ]

    @Unroll
    def "An admin user should have access to the actuator endpoint #endpoint"(endpoint) {
        given:
        String adminUser = "admin1"
        String password = "password"
        userHelper.deleteUser(adminUser)
        userHelper.createUser(adminUser, password, "ADMIN")

        when:
        go endpoint

        then:
        at LoginPage

        when:
        login adminUser, password

        then:
        currentUrl.endsWith endpoint
        !$('h1')

        where:
        endpoint << ENDPOINTS
    }

    @Unroll
    def "A non admin user should not have access to the actuator endpoint #endpoint"(endpoint) {
        given:
        String user = "user1"
        String password = "password"
        userHelper.deleteUser(user)
        userHelper.createUser(user, password, "OTHER_ROLE")

        when:
        go endpoint

        then:
        at LoginPage

        when:
        login user, password

        then:
        currentUrl.endsWith endpoint
        $('h1').text() == '403'

        where:
        endpoint << ENDPOINTS
    }
}
