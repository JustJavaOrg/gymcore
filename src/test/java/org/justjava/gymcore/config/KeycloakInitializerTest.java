package org.justjava.gymcore.config;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.justjava.gymcore.model.UserRole;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakInitializerTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private RealmsResource realmsResource;

    @Mock
    private ClientsResource clientsResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private Response response;

    @InjectMocks
    private KeycloakInitializer keycloakInitializer;

    private final String TEST_REALM = "test-realm";
    private final String TEST_FRONTEND_CLIENT_ID = "test-frontend-client";
    private final String TEST_BEARER_ONLY_CLIENT_ID = "test-bearer-only-client";
    private final String TEST_FRONTEND_ORIGIN_URL = "http://localhost:3000";
    private final String TEST_FRONTEND_REDIRECT_URL = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(keycloakInitializer, "realm", TEST_REALM);
        ReflectionTestUtils.setField(keycloakInitializer, "frontendClientId", TEST_FRONTEND_CLIENT_ID);
        ReflectionTestUtils.setField(keycloakInitializer, "bearerOnlyClientId", TEST_BEARER_ONLY_CLIENT_ID);
        ReflectionTestUtils.setField(keycloakInitializer, "frontendOriginUrl", TEST_FRONTEND_ORIGIN_URL);
        ReflectionTestUtils.setField(keycloakInitializer, "frontendRedirectUrl", TEST_FRONTEND_REDIRECT_URL);
    }

    @Test
    void initializeKeycloak_shouldCreateRealmClientsAndRoles_whenRealmDoesNotExist() {
        // Arrange
        when(keycloak.realm(TEST_REALM)).thenReturn(realmResource);
        when(keycloak.realms()).thenReturn(realmsResource);

        // Simulate realm not found
        when(realmResource.toRepresentation()).thenThrow(new NotFoundException("Realm not found"));

        // Mock realm creation
        doNothing().when(realmsResource).create(any(RealmRepresentation.class));

        // Mock clients
        when(realmResource.clients()).thenReturn(clientsResource);
        when(clientsResource.findByClientId(TEST_FRONTEND_CLIENT_ID)).thenReturn(new ArrayList<>());
        when(clientsResource.findByClientId(TEST_BEARER_ONLY_CLIENT_ID)).thenReturn(new ArrayList<>());
        when(clientsResource.create(any(ClientRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());
        when(response.getLocation()).thenReturn(java.net.URI.create("http://localhost:8080/admin/realms/test-realm/clients/123"));

        // Mock roles
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.list()).thenReturn(new ArrayList<>());

        // Act
        keycloakInitializer.initializeKeycloak();

        // Assert
        // Verify realm creation
        ArgumentCaptor<RealmRepresentation> realmCaptor = ArgumentCaptor.forClass(RealmRepresentation.class);
        verify(realmsResource).create(realmCaptor.capture());
        RealmRepresentation capturedRealm = realmCaptor.getValue();
        assertEquals(TEST_REALM, capturedRealm.getRealm());
        assertTrue(capturedRealm.isEnabled());

        // Verify client creation
        ArgumentCaptor<ClientRepresentation> clientCaptor = ArgumentCaptor.forClass(ClientRepresentation.class);
        verify(clientsResource, times(2)).create(clientCaptor.capture());
        List<ClientRepresentation> capturedClients = clientCaptor.getAllValues();

        // First client should be frontend client
        ClientRepresentation frontendClient = capturedClients.get(0);
        assertEquals(TEST_FRONTEND_CLIENT_ID, frontendClient.getClientId());
        assertTrue(frontendClient.isPublicClient());
        assertTrue(frontendClient.isEnabled());

        // Second client should be bearer-only client
        ClientRepresentation bearerOnlyClient = capturedClients.get(1);
        assertEquals(TEST_BEARER_ONLY_CLIENT_ID, bearerOnlyClient.getClientId());
        assertTrue(bearerOnlyClient.isBearerOnly());
        assertTrue(bearerOnlyClient.isEnabled());

        // Verify role creation
        ArgumentCaptor<RoleRepresentation> roleCaptor = ArgumentCaptor.forClass(RoleRepresentation.class);
        verify(rolesResource, times(UserRole.values().length)).create(roleCaptor.capture());
        List<RoleRepresentation> capturedRoles = roleCaptor.getAllValues();

        // Verify all UserRole enum values were created
        for (int i = 0; i < UserRole.values().length; i++) {
            assertEquals(UserRole.values()[i].name(), capturedRoles.get(i).getName());
        }
    }

    @Test
    void initializeKeycloak_shouldNotCreateRealm_whenRealmExists() {
        // Arrange
        when(keycloak.realm(TEST_REALM)).thenReturn(realmResource);

        // Simulate realm exists
        when(realmResource.toRepresentation()).thenReturn(new RealmRepresentation());

        // Mock clients
        when(realmResource.clients()).thenReturn(clientsResource);

        // Mock existing clients
        List<ClientRepresentation> frontendClients = new ArrayList<>();
        ClientRepresentation frontendClient = new ClientRepresentation();
        frontendClient.setClientId(TEST_FRONTEND_CLIENT_ID);
        frontendClients.add(frontendClient);
        when(clientsResource.findByClientId(TEST_FRONTEND_CLIENT_ID)).thenReturn(frontendClients);

        List<ClientRepresentation> bearerClients = new ArrayList<>();
        ClientRepresentation bearerClient = new ClientRepresentation();
        bearerClient.setClientId(TEST_BEARER_ONLY_CLIENT_ID);
        bearerClients.add(bearerClient);
        when(clientsResource.findByClientId(TEST_BEARER_ONLY_CLIENT_ID)).thenReturn(bearerClients);

        // Mock roles
        when(realmResource.roles()).thenReturn(rolesResource);

        // Mock existing roles
        List<RoleRepresentation> existingRoles = new ArrayList<>();
        for (UserRole role : UserRole.values()) {
            RoleRepresentation roleRep = new RoleRepresentation();
            roleRep.setName(role.name());
            existingRoles.add(roleRep);
        }
        when(rolesResource.list()).thenReturn(existingRoles);

        // Act
        keycloakInitializer.initializeKeycloak();

        // Assert
        // Verify realm was not created
        verify(keycloak, never()).realms();

        // Verify clients were not created
        verify(clientsResource, never()).create(any(ClientRepresentation.class));

        // Verify roles were not created
        verify(rolesResource, never()).create(any(RoleRepresentation.class));
    }

    @Test
    void initializeKeycloak_shouldRetry_whenExceptionOccurs() {
        // Arrange
        // First call throws exception, second call succeeds
        when(keycloak.realm(TEST_REALM))
            .thenThrow(new RuntimeException("Connection error"))
            .thenReturn(realmResource);

        when(realmResource.toRepresentation()).thenReturn(new RealmRepresentation());
        when(realmResource.clients()).thenReturn(clientsResource);

        // Mock existing clients
        List<ClientRepresentation> frontendClients = new ArrayList<>();
        ClientRepresentation frontendClient = new ClientRepresentation();
        frontendClient.setClientId(TEST_FRONTEND_CLIENT_ID);
        frontendClients.add(frontendClient);
        when(clientsResource.findByClientId(TEST_FRONTEND_CLIENT_ID)).thenReturn(frontendClients);

        List<ClientRepresentation> bearerClients = new ArrayList<>();
        ClientRepresentation bearerClient = new ClientRepresentation();
        bearerClient.setClientId(TEST_BEARER_ONLY_CLIENT_ID);
        bearerClients.add(bearerClient);
        when(clientsResource.findByClientId(TEST_BEARER_ONLY_CLIENT_ID)).thenReturn(bearerClients);

        // Mock roles
        when(realmResource.roles()).thenReturn(rolesResource);

        // Mock existing roles
        List<RoleRepresentation> existingRoles = new ArrayList<>();
        for (UserRole role : UserRole.values()) {
            RoleRepresentation roleRep = new RoleRepresentation();
            roleRep.setName(role.name());
            existingRoles.add(roleRep);
        }
        when(rolesResource.list()).thenReturn(existingRoles);

        // Act
        keycloakInitializer.initializeKeycloak();

        // Assert
        // Verify keycloak.realm was called at least twice (once for the exception, at least once for the success)
        verify(keycloak, atLeast(2)).realm(TEST_REALM);
    }
}
