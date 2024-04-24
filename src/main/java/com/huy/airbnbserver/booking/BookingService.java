package com.huy.airbnbserver.booking;

import com.huy.airbnbserver.properties.PropertyRepository;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.system.exception.EntityAlreadyExistException;
import com.huy.airbnbserver.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public Booking save(Booking booking, Integer userId, Long propertyId) {
        var user = userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        var property = propertyRepository.findById(propertyId).orElseThrow(()->new ObjectNotFoundException("property", propertyId));
        if (!bookingRepository.findByUserIdAndPropertyId(userId, propertyId).isEmpty()) {
            throw new EntityAlreadyExistException("booking entity with this userId and propertyId");
        }

        booking.addUser(user);
        booking.addProperty(property);
        booking.setConfirm(false);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookingByUserId(Integer userId) {
        userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        return bookingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookingByPropertyId(Long propertyId, Integer userId) {
        userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        var property = propertyRepository
                .findById(propertyId)
                .orElseThrow(()->new ObjectNotFoundException("property", propertyId));
        if (!property.getHost().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied for this user");
        }
        return bookingRepository.findByPropertyId(propertyId);
    }

    @Transactional
    public void delete(Long id, Integer userId) {
        userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        var deletedBooking = bookingRepository.findById(id).orElseThrow(()->new ObjectNotFoundException("booking", id));

        if (!userId.equals(deletedBooking.getUser().getId())) {
            throw new AccessDeniedException("Access denied for this user");
        }

        deletedBooking.cancel();
        bookingRepository.delete(deletedBooking);
    }

    @Transactional
    public void confirm(Long id, Integer userId) {
        userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        var confirmBooking = bookingRepository.findById(id).orElseThrow(()->new ObjectNotFoundException("booking", id));
        if (!confirmBooking.getProperty().getHost().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied for this user");
        }

        confirmBooking.setConfirm(true);
        bookingRepository.save(confirmBooking);
    }
}
