package org.justjava.gymcore.config;

import org.keycloak.admin.client.Keycloak;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestKeycloakConfig {

    @Bean
    @Primary
    public Keycloak keycloak() {
        // Create a mock Keycloak instance
        return Mockito.mock(Keycloak.class);
    }
}
