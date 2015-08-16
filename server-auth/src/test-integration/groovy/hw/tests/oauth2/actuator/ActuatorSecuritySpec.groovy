package hw.tests.oauth2.actuator

import spock.lang.Unroll

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

import hw.tests.oauth2.HwOauth2Spec
import hw.tests.oauth2.pages.LoginPage
import hw.tests.oauth2.utils.UserHelper

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
        validateJson($('body').text())

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

    /**
     * Validate if {@code json} is valid JSON.
     *
     * Throws an exception if {@code json} does not contain a valid JSON string.
     */
    private void validateJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
        objectMapper.readTree(json)
    }
}
