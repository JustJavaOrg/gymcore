package org.justjava.gymcore.service;

import org.justjava.gymcore.model.User;
import org.justjava.gymcore.model.UserRole;
import org.justjava.gymcore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private KeycloakService keycloakService;

    @Test
    void createUser_savesUser() {
        var user = new User("Alice", "alice@example.com", UserRole.MEMBER, null);
        var savedUser = new User("Alice", "alice@example.com", UserRole.MEMBER, null);
        savedUser.setId(1L);
        when(userRepository.save(user)).thenReturn(savedUser);

        var result = userService.createUser(user);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());

        verify(userRepository).save(user);
    }

    @Test
    void getUser_returnsEmpty_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUser(99L);
        assertTrue(result.isEmpty());
        verify(userRepository).findById(99L);
    }
}
