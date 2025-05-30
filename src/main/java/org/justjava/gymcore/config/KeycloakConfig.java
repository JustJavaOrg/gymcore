package org.justjava.gymcore.config;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class KeycloakConfig {

    @Value("${keycloak.server.url}")
    public String serverUrl;

    @Value("${keycloak.user.name}")
    public String username;

    @Value("${keycloak.password}")
    public String password;

    @Value("${spring.keycloak.admin.timeout-seconds:10}")
    private Integer adminTimeoutSeconds;



    @Bean
    public Keycloak keycloak() {
        // Configure keycloak client with retries
        String initialRealm = "master";
        String initialClientId = "admin-cli";
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(initialRealm)
                .clientId(initialClientId)
                .username(username)
                .password(password)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .connectTimeout(adminTimeoutSeconds, TimeUnit.SECONDS)
                        .build())
                .build();
    }
}


