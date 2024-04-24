package com.huy.airbnbserver.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT * FROM Booking b WHERE b.user_id = :userId", nativeQuery = true)
    List<Booking> findByUserId(Integer userId);

    @Query(value = "SELECT * FROM Booking b WHERE b.property_id = :propertyId", nativeQuery = true)
    List<Booking> findByPropertyId(Long propertyId);

    @Query(value = "SELECT * FROM Booking b WHERE b.user_id = :userId AND b.property_id = :propertyId", nativeQuery = true)
    List<Booking> findByUserIdAndPropertyId(Integer userId, Long propertyId);
}
