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
//    private final KeycloakAdminClientService keycloakAdminClientService;

    public User createUser(User user) {
        log.info("Creating new user");
//        keycloakAdminClientService.addUser(user);
        return userRepository.save(user);
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        checkIfBookingExists(id);
        userDetails.setId(id);
        return userRepository.save(userDetails);
    }

    public void deleteUser(Long id) {
        checkIfBookingExists(id);
        userRepository.deleteById(id);
    }

    private void checkIfBookingExists(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Cannot proceed - User ID {} not found", id);
            throw new IllegalArgumentException("User not found");
        }
    }
}
