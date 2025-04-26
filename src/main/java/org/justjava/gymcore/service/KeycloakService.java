package org.justjava.gymcore.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {


    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    public String realm;

    public void     addUser(User user) {

        UsersResource usersResource = keycloak.realm(realm).users();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getEmail());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true); // Todo: If user verification is enabled, set to false

        try (Response response = usersResource.create(userRepresentation)) {
            if(response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("Created user {}", user.getEmail());
                RoleRepresentation roleRepresentation = keycloak.realm(realm).roles().get(user.getRole().name()).toRepresentation();
                usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
            } else {
                log.error("Failed to create user {}, HTTP status: {}", user.getEmail(), response.getStatus());
                throw new RuntimeException("Failed to create user");
            }
        } catch (Exception e) {
            log.error("Failed to create user {}", user.getEmail(), e);
            throw new RuntimeException("Failed to create user");
        }

    }

    private List<CredentialRepresentation> createPasswordCredential(String password) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        return Collections.singletonList(cred);
    }

    public void updateUser(String userId, User newUser) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(userId);

        UserRepresentation existingUser = userResource.toRepresentation();

        UserRepresentation updatedUser = new UserRepresentation(existingUser);

        if (newUser.getEmail() != null) {
            updatedUser.setEmail(newUser.getEmail());
            updatedUser.setUsername(newUser.getEmail());
        } else {
            throw new IllegalArgumentException("Email is required");
        }

        userResource.update(updatedUser);
    }

    public void deleteUser(String userId) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(userId);
        if(userResource == null) {
            log.warn("User {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }
        try(Response response = usersResource.delete(userId)){
            if(response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                log.info("Deleted user {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to delete user {}", userId, e);
            throw new RuntimeException("Failed to delete user");
        }
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(userId);
        if (userResource == null) {
            log.warn("User {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }

        if(!oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UserRepresentation updatedUser = new UserRepresentation(userResource.toRepresentation());

        updatedUser.setCredentials(createPasswordCredential(newPassword));
        userResource.update(updatedUser);
    }
}
