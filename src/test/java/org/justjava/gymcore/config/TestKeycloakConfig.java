package org.justjava.gymcore.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import jakarta.ws.rs.core.Response;
import static org.mockito.ArgumentMatchers.any;



import java.net.URI;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestKeycloakConfig {

    @Bean
    @Primary
    public Keycloak keycloak() {
        // Create mock objects for the Keycloak client hierarchy
        Keycloak keycloak = mock(Keycloak.class);
        RealmResource realmResource = mock(RealmResource.class);
        RealmsResource realmsResource = mock(RealmsResource.class);
        RolesResource rolesResource = mock(RolesResource.class);
        ClientsResource clientsResource = mock(ClientsResource.class);
        Response response = mock(Response.class);

        // Configure the mocks to return appropriate values
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(keycloak.realms()).thenReturn(realmsResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(realmResource.clients()).thenReturn(clientsResource);
        when(rolesResource.list()).thenReturn(new ArrayList<>());
        when(clientsResource.findByClientId(anyString())).thenReturn(new ArrayList<>());
        when(clientsResource.create(any(ClientRepresentation.class))).thenReturn(response);
        when(realmResource.toRepresentation()).thenReturn(new RealmRepresentation());

        doNothing().when(realmsResource).create(any(RealmRepresentation.class));
        doNothing().when(rolesResource).create(any(RoleRepresentation.class));

        // Mock response status and location
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());
        try {
            when(response.getLocation()).thenReturn(new URI("http://localhost/auth/admin/realms/master/clients/123"));
        } catch (Exception e) {
            // Ignore URI syntax exception in test
        }

        return keycloak;
    }

    @Bean
    @Primary
    public KeycloakInitializer keycloakInitializer(Keycloak keycloak) {
        // Create a mock KeycloakInitializer that does nothing in its @PostConstruct method

        // No need to define behavior since we're mocking the entire bean
        // The @PostConstruct method won't be called on a mock

        return mock(KeycloakInitializer.class);
    }
}
