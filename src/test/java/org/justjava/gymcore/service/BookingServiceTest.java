package org.justjava.gymcore.service;

import org.justjava.gymcore.model.Booking;
import org.justjava.gymcore.model.GymClass;
import org.justjava.gymcore.model.User;
import org.justjava.gymcore.model.UserRole;
import org.justjava.gymcore.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_savesBooking() {
        var member = new User("Member", "member@example.com", UserRole.MEMBER, null);
        member.setId(1L);
        var trainer = new User("Trainer", "trainer@example.com", UserRole.TRAINER, null);
        trainer.setId(2L);
        var gymClass = new GymClass("Spinning", "Indoor cycling", LocalDateTime.of(2025, 2, 5, 9, 0), 20, trainer);
        gymClass.setId(10L);
        var booking = new Booking(member, gymClass);
        var savedBooking = new Booking(member, gymClass);
        savedBooking.setId(100L);
        when(bookingRepository.save(booking)).thenReturn(savedBooking);

        var result = bookingService.createBooking(booking);
        assertNotNull(result);
        assertEquals(100L, result.getId());

        verify(bookingRepository).save(booking);
    }

    @Test
    void getBooking_returnsEmpty_whenNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Booking> result = bookingService.getBooking(999L);
        assertTrue(result.isEmpty());
        verify(bookingRepository).findById(999L);
    }
}
