package org.justjava.gymcore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.justjava.gymcore.model.GymClass;
import org.justjava.gymcore.model.UserRole;
import org.justjava.gymcore.model.Booking;
import org.justjava.gymcore.model.User;
import org.justjava.gymcore.model.dto.ResponseDetail;
import org.justjava.gymcore.repository.BookingRepository;
import org.justjava.gymcore.repository.GymClassRepository;
import org.justjava.gymcore.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final GymClassRepository gymClassRepository;

    private final Map<Long, List<User>> waitlist = new ConcurrentHashMap<>();

    public ResponseEntity<?> createBooking(Booking booking) throws BadRequestException {
        log.info("Creating new booking for user: {}", booking.getUser());

        validateUsers(booking);
        validateGymClassTimePeriod(booking);

        GymClass gymClass = booking.getGymClass();

        if (isClassFull(gymClass)) {
            return addToWaitlist(booking.getUser(), gymClass);
        }

        return confirmBooking(booking);
    }

    private ResponseEntity<?> confirmBooking(Booking booking) {
        Booking bookedEntity = bookingRepository.save(booking);
        log.info("Booking confirmed for user {} in class {}", booking.getUser().getId(), booking.getGymClass().getId());
        return new ResponseEntity<>(bookedEntity, HttpStatus.CREATED);
    }

    private ResponseEntity<?> addToWaitlist(User user, GymClass gymClass) {
        waitlist.computeIfAbsent(gymClass.getId(), k -> new ArrayList<>()).add(user);
        log.info("User {} added to waitlist for class {}", user.getId(), gymClass.getId());
        return new ResponseEntity<>(new ResponseDetail("Class is full. You are now on the waitlist."), HttpStatus.OK);
    }

    private boolean checkTimePeriodForTrainer(Booking booking) {
        LocalDateTime bookingStartTime = booking.getGymClass().getScheduledAt();
        LocalDateTime bookingEndTime = booking.getGymClass().getScheduleEnd();
        boolean isTrainerAvailable = this.gymClassRepository.existsByUserIdAndBookingTimePeriod(booking.getGymClass().getTrainer().getId(), bookingStartTime, bookingEndTime);
        return isTrainerAvailable;
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

    private void createGymClass(Booking booking) {
        GymClass gymClass = new GymClass(booking.getGymClass().getTitle(), booking.getGymClass().getDescription(),
                booking.getGymClass().getScheduledAt(), booking.getGymClass().getScheduleEnd(),
                booking.getGymClass().getCapacity(), booking.getGymClass().getTrainer());
        booking.setGymClass(gymClassRepository.save(gymClass));
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
        long confirmedBookings = bookingRepository.countByGymClass(gymClass);
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
        List<User> waitlistedUsers = waitlist.get(gymClass.getId());
        if (waitlistedUsers != null && !waitlistedUsers.isEmpty()) {
            User nextUser = waitlistedUsers.remove(0);
            Booking promotedBooking = new Booking(nextUser, gymClass);
            bookingRepository.save(promotedBooking);

            log.info("User {} moved from waitlist to class {}", nextUser.getId(), gymClass.getId());

            if (waitlistedUsers.isEmpty()) {
                waitlist.remove(gymClass.getId());
            }
        }
    }


        private void checkIfBookingExists (Long id){
            if (!bookingRepository.existsById(id)) {
                log.warn("Cannot proceed - Booking ID {} not found", id);
                throw new IllegalArgumentException("Booking not found");
            }
        }
    }
