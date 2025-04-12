package org.justjava.gymcore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.justjava.gymcore.model.*;
import org.justjava.gymcore.model.dto.ResponseDetail;
import org.justjava.gymcore.repository.BookingRepository;
import org.justjava.gymcore.repository.GymClassRepository;
import org.justjava.gymcore.repository.UserRepository;
import org.justjava.gymcore.repository.WaitlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final GymClassRepository gymClassRepository;
    private final WaitlistRepository waitlistRepository;

    public ResponseEntity<?> createBooking(Booking booking) throws BadRequestException {
        log.info("Creating new booking for user: {}", booking.getUser());

        validateUsers(booking);
        validateGymClassTimePeriod(booking);
        validateTrainerAvailability(booking);

        GymClass gymClass = booking.getGymClass();

        if (isClassFull(gymClass)) {
            return addToWaitlist(booking.getUser(), gymClass);
        }

        return confirmBooking(booking);
    }

    private ResponseEntity<?> confirmBooking(Booking booking) {
        Booking bookedEntity = bookingRepository.save(booking);
        log.info("Booking confirmed for user {} in GymClass {}", booking.getUser().getId(), booking.getGymClass().getId());
        return new ResponseEntity<>(bookedEntity, HttpStatus.CREATED);
    }

    private ResponseEntity<?> addToWaitlist(User user, GymClass gymClass) {

        if (waitlistRepository.existsByUserIdAndGymClassId(user.getId(), gymClass.getId())) {
            return new ResponseEntity<>(new ResponseDetail("You are already on the waitlist."), HttpStatus.OK);
        }

        Waitlist waitlistEntry = new Waitlist(user, gymClass);
        waitlistRepository.save(waitlistEntry);
        log.info("User {} added to waitlist for GymClass {}", user.getId(), gymClass.getId());
        return new ResponseEntity<>(new ResponseDetail("GymClass is full. You are now on the waitlist."), HttpStatus.OK);
    }

    private void validateTrainerAvailability(Booking booking) throws BadRequestException {
        Long trainerId = booking.getGymClass().getTrainer().getId();
        LocalDateTime start = booking.getGymClass().getScheduledAt();
        LocalDateTime end = booking.getGymClass().getScheduleEnd();

        List<Booking> overlappingBookings = bookingRepository.findOverlappingTrainerBookings(trainerId, start, end);

        if (!overlappingBookings.isEmpty()) {
            throw new BadRequestException("Trainer is already booked during this time.");
        }
    }


    private void validateGymClassTimePeriod(Booking booking) throws BadRequestException {
        if (booking.getGymClass().getScheduledAt().isAfter(booking.getGymClass().getScheduleEnd()))
            throw new BadRequestException("Time period is not valid.");
    }

    private void validateUsers(Booking booking) throws BadRequestException {
        User trainer = userRepository.findByUserIdAndUserRole(booking.getGymClass().getTrainer().getId(), UserRole.TRAINER);
        if (Objects.isNull(trainer))
            throw new BadRequestException("Trainer can not be found");

        User trainee = userRepository.findByUserIdAndUserRole(booking.getUser().getId(), UserRole.MEMBER);
        if (Objects.isNull(trainee))
            throw new BadRequestException("Member can not be found");
    }

        public Optional<Booking> getBooking (Long id){
            return bookingRepository.findById(id);
        }

        public List<Booking> getAllBookings () {
            return bookingRepository.findAll();
        }

        public Booking updateBooking (Long id, Booking bookingDetails){
            checkIfBookingExists(id);
            bookingDetails.setId(id);
            return bookingRepository.save(bookingDetails);
        }

    private boolean isClassFull(GymClass gymClass) {
        long confirmedBookings = bookingRepository.countByGymClassId(gymClass.getId());
        return confirmedBookings >= gymClass.getCapacity();
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        bookingRepository.delete(booking);
        log.info("Booking ID {} deleted.", id);

        promoteWaitlistedUser(booking.getGymClass());
    }

    private void promoteWaitlistedUser(GymClass gymClass) {
        List<Waitlist> waitlistedUsers = waitlistRepository.findByGymClassId(gymClass.getId());
        if (!waitlistedUsers.isEmpty()) {
            Waitlist nextInLine = waitlistedUsers.get(0);
            Booking promotedBooking = new Booking(nextInLine.getUser(), gymClass);
            bookingRepository.save(promotedBooking);
            waitlistRepository.delete(nextInLine);

            log.info("User {} moved from waitlist to class {}", nextInLine.getUser().getId(), gymClass.getId());
        }
    }


        private void checkIfBookingExists (Long id){
            if (!bookingRepository.existsById(id)) {
                log.warn("Cannot proceed - Booking ID {} not found", id);
                throw new IllegalArgumentException("Booking not found");
            }
        }
    }
