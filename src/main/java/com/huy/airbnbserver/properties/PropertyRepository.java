package com.huy.airbnbserver.properties;

import jakarta.annotation.Nonnull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

//    @Query(value =
//            "SELECT DISTINCT p.* " +
//            "FROM Property p " +
//            "JOIN user_account h ON p.host_id = h.id " +
//            "LEFT JOIN image i ON p.id = i.property_id " +
//            "WHERE p.id = :id",
//            nativeQuery = true)
//    @Nonnull
//    Optional<Property> findById(@Nonnull Long id);

    @Query(value =
                "SELECT p " +
                "FROM Property p " +
                "JOIN p.likedByUsers u " +
                "WHERE u.id = :userId")
    List<Property> getLikedByUserId(Integer userId);

    @Query(value = "SELECT * FROM liked_property l WHERE l.user_id = :userId AND l.property_id = :propertyId", nativeQuery = true)
    List<Object> getLikedDetailsOfUserIdAndPropertyId(Integer userId, Long propertyId);
}
