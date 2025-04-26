package org.justjava.gymcore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.justjava.gymcore.model.User;
import org.justjava.gymcore.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService
            keycloakService;

    public User createUser(User user) {
        log.info("Creating new user");
        User newUser;
        try {
            newUser = userRepository.save(user);
            keycloakService.addUser(newUser);
        } catch (Exception e) {
            log.error("Error creating new user", e);
            throw new RuntimeException("Error creating new user");
        }
        return newUser;
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        checkIfBookingExists(id);
        User updatedUser;
        try {
            userDetails.setId(id);
            updatedUser = userRepository.save(userDetails);
            keycloakService.updateUser(id.toString(), updatedUser);
        } catch (Exception e) {
            log.error("Error updating user", e);
            throw new RuntimeException("Error updating user");
        }
        return updatedUser;
    }

    public void deleteUser(Long id) {
        checkIfBookingExists(id);
        try {
            userRepository.deleteById(id);
            keycloakService.deleteUser(id.toString());
        } catch (Exception e) {
            log.error("Error deleting user", e);
            throw new RuntimeException("Error deleting user");
        }
    }

    private void checkIfBookingExists(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Cannot proceed - User ID {} not found", id);
            throw new IllegalArgumentException("User not found");
        }
    }
}
