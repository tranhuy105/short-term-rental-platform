package com.huy.airbnbserver.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT * FROM Booking b WHERE b.user_id = :userId", nativeQuery = true)
    List<Booking> findByUserId(Integer userId);

    @Query(value = "SELECT * FROM Booking b WHERE b.property_id = :propertyId", nativeQuery = true)
    List<Booking> findByPropertyId(Long propertyId);

    @Query(value = """
            SELECT 
                DISTINCT DATE(check_in_date) AS booking_date
            FROM booking WHERE property_id = :propertyId 
            UNION 
            SELECT 
                DISTINCT DATE(check_out_date) AS booking_date
            FROM booking WHERE property_id = :propertyId""", nativeQuery = true)
    List<Date> findAllBookingDateOfProperty(@NonNull Long propertyId);
}
