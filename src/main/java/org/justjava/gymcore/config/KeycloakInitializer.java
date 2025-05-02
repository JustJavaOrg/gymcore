package org.justjava.gymcore.config;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.justjava.gymcore.model.UserRole;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakInitializer {

    @Value("${frontend.origin.url}")
    private String frontendOriginUrl;

    @Value("${frontend.redirect.url}")
    private String frontendRedirectUrl;

    @Value("${keycloak.frontend.client.id}")
    public String frontendClientId;

    @Value("${keycloak.bearer.only.client.id}")
    public String bearerOnlyClientId;

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    @PostConstruct
    public void initializeKeycloak() {
        log.info("Initializing Keycloak configuration for realm: {}", realm);

        // Retry logic to handle transient Keycloak service unavailability
        retry(() -> {
            createRealm();
            createClients();
            createRoles();
        }, 5, Duration.ofSeconds(2));
    }

    private void createRoles() {
        List<RoleRepresentation> roles =  keycloak.realm(realm).roles().list();
        for (UserRole role : UserRole.values()) {
            boolean roleExists =roles.stream()
                    .anyMatch(r -> r.getName().equals(role.name()));
            if (!roleExists) {
                RoleRepresentation roleRepresentation = new RoleRepresentation();
                roleRepresentation.setName(role.name());
                try {
                    keycloak.realm(realm).roles().create(roleRepresentation);
                    log.info("Created role: {}", role.name());
                } catch (Exception e) {
                    log.error("Failed to create role {}: {}", role.name(), e.getMessage());
                    throw new RuntimeException("Failed to create role " + role.name());
                }
            } else {
                log.info("Role {} already exists.", role.name());
            }
        }
    }

    private void createRealm(){
        // create realm if it doesn't exist
        try {
            keycloak.realm(realm).toRepresentation();
            log.info("Realm {} already exists", realm);
        } catch (NotFoundException e) {
            log.info("Creating realm {}", realm);
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(realm);
            realmRepresentation.setEnabled(true);
            realmRepresentation.setDisplayName(realm);
            keycloak.realms().create(realmRepresentation);
        }
    }

    private void retry(Runnable task, int maxRetries, Duration delay) {
        int attempt = 1;

        while (true){
            try {
                task.run();
                return;
            } catch (Exception e) {
                if(attempt >= maxRetries) {
                    log.error("Failed to initialize Keycloak after {} retries", maxRetries);
                    throw new RuntimeException(e);
                }
                log.warn("Retry {}/{} initializing Keycloak...", attempt, maxRetries);
                attempt++;
                try{
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Thread interrupted during retry delay", ie);
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    private void createClients() {
        // frontend client
        ClientsResource clientsResource = keycloak.realm(realm).clients();
        ClientRepresentation frontEndClientRepresentation = clientsResource.findByClientId(frontendClientId).stream().findFirst().orElse(null);
        if(frontEndClientRepresentation == null) {
            ClientRepresentation frontEndClient = getFrontEndClient();
            try (Response response = keycloak.realm(realm).clients().create(frontEndClient)) {
                // optionally process the response
                handleClientCreationResponse(response, "frontend client");
            } catch (Exception e) {
                log.error("failed to create frontend client: {}", e.getMessage());
                throw new RuntimeException("Failed to create frontend client", e);
            }
        }

        ClientRepresentation bearerOnlyClientRepresentation = clientsResource.findByClientId(bearerOnlyClientId).stream().findFirst().orElse(null);

        if (bearerOnlyClientRepresentation == null){// bearer-only client
            ClientRepresentation bearerOnlyClient = getBearerOnlyClient();

            // Add to Keycloak
            try (Response response = keycloak.realm(realm).clients().create(bearerOnlyClient)) {
                handleClientCreationResponse(response, "bearer-only client");
            } catch (Exception e) {
                log.error("Failed to create bearer-only client: {}", e.getMessage());
                throw new RuntimeException("failed to create bearer-only client", e);
            }
        }
    }

    /**
     * Handles Keycloak API responses for client creation.
     * @param response HTTP response from Keycloak
     * @param clientType Type of client (e.g., "frontend client")
     */
    private void handleClientCreationResponse(Response response, String clientType) {
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            String clientId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Created {} with ID: {}", clientType, clientId);
        } else {
            log.error("Failed to create {}, HTTP status: {}", clientType, response.getStatus());
            throw new RuntimeException("Failed to create " + clientType + ", HTTP status: " + response.getStatus());
        }
    }


    private ClientRepresentation getBearerOnlyClient() {
        ClientRepresentation bearerOnlyClient = new ClientRepresentation();
        bearerOnlyClient.setClientId(bearerOnlyClientId);
        bearerOnlyClient.setName("My API Service");  // Human-readable name
        bearerOnlyClient.setEnabled(true);
        bearerOnlyClient.setBearerOnly(true);  // Disables all interactive flows
        bearerOnlyClient.setStandardFlowEnabled(false);  // Explicitly disable auth code flow
        bearerOnlyClient.setDirectAccessGrantsEnabled(false);  // Disable password flow
        bearerOnlyClient.setServiceAccountsEnabled(true);  // Enable if machine-to-machine auth is needed
        // Optional but recommended:
        bearerOnlyClient.setAttributes(Map.of(
                "exclude.session.state.from.auth.response", "true",
                "oauth2.device.authorization.grant.enabled", "false"
        ));
        return bearerOnlyClient;
    }

    private ClientRepresentation getFrontEndClient() {
        ClientRepresentation frontEndClient = new ClientRepresentation();
        frontEndClient.setClientId(frontendClientId);
        frontEndClient.setEnabled(true);
        frontEndClient.setPublicClient(true);
        frontEndClient.setRedirectUris(List.of(frontendRedirectUrl+"/*"));
        frontEndClient.setWebOrigins(List.of(frontendOriginUrl));
        frontEndClient.setAuthorizationServicesEnabled(false);
        frontEndClient.setStandardFlowEnabled(true);
        frontEndClient.setDirectAccessGrantsEnabled(false);
        return frontEndClient;
    }
}
