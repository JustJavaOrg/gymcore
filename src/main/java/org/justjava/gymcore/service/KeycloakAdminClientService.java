package org.justjava.gymcore.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.justjava.gymcore.config.KeycloakConfig;
import org.justjava.gymcore.model.User;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakAdminClientService {


    private final KeycloakConfig keycloakConfig;

    @Value("${keycloak.realm}")
    private String realm;

//    private final Keycloak keycloak;
//
//    public void addUser(User user) {
//
//        if(keycloak == null) {
//            log.error("Keycloak client is not initialized");
//            return;
//        }
//
//        UsersResource usersResource = keycloak.realm(realm).users();
//
//        CredentialRepresentation credential = createPasswordCredential(user.getPassword());
//
//        UserRepresentation userRepresentation = new UserRepresentation();
//        userRepresentation.setUsername(user.getEmail());
//        userRepresentation.setEmail(user.getEmail());
//        userRepresentation.setEnabled(true);
//        userRepresentation.setEmailVerified(true); // Todo: If user verification is enabled, set to false
//        userRepresentation.setCredentials(Collections.singletonList(credential));
//
//        try (Response response = usersResource.create(userRepresentation)) {
//            if(response.getStatus() == Response.Status.CREATED.getStatusCode()) {
//                String userId = CreatedResponseUtil.getCreatedId(response);
//                log.info("Created user {}", user.getEmail());
//                RoleRepresentation roleRepresentation = keycloak.realm(realm).roles().get(user.getRole().name()).toRepresentation();
//                usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
//            } else {
//                log.error("Failed to create user {}, HTTP status: {}", user.getEmail(), response.getStatus());
//            }
//        }
//
//    }

    private static CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        return cred;
    }
}
