package com.huy.airbnbserver.properties;

import com.huy.airbnbserver.properties.category.Area;
import com.huy.airbnbserver.properties.category.Category;
import com.huy.airbnbserver.properties.category.Tag;
import com.huy.airbnbserver.properties.dto.ReviewInfoProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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

    @Query(value = "SELECT * " +
                "FROM liked_property l " +
                "WHERE l.user_id = :userId AND l.property_id = :propertyId"
            , nativeQuery = true)
    List<Object> getLikedDetailsOfUserIdAndPropertyId(Integer userId, Long propertyId);

    @NonNull
    @Query(value = "SELECT DISTINCT p FROM Property p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.host h " +
            "LEFT JOIN FETCH h.avatar " +
            "WHERE p.id = :id ")
    Optional<Property> findDetailById(@NonNull Long id);

    @Query(value = """
        SELECT p FROM Property p 
        LEFT JOIN FETCH p.images 
        WHERE (:category1 IS NULL OR :category1 MEMBER OF p.categories)
        AND (:category2 IS NULL OR :category2 MEMBER OF p.categories)
        AND (:tag IS NULL OR p.tag = :tag)
        AND (:area IS NULL OR 
            (p.longitude BETWEEN :minLongitude AND :maxLongitude) AND
            (p.latitude BETWEEN :minLatitude AND :maxLatitude))
        AND (:minNightlyPrice IS NULL OR :maxNightlyPrice IS NULL OR
            p.nightlyPrice BETWEEN :minNightlyPrice AND :maxNightlyPrice)
        AND (:minBeds IS NULL OR p.numBeds >= :minBeds)
        AND (:minBathrooms IS NULL OR p.numBathrooms >= :minBathrooms)
        AND (:minBedrooms IS NULL OR p.numBedrooms >= :minBedrooms)  
        """)
    List<Property> findAllCustom(@Nullable Category category1,
                                 @Nullable Category category2,
                                 @Nullable Tag tag,
                                 @Nullable Area area,
                                 @Nullable Double minLongitude,
                                 @Nullable Double maxLongitude,
                                 @Nullable Double minLatitude,
                                 @Nullable Double maxLatitude,
                                 @Nullable Double minNightlyPrice,
                                 @Nullable Double maxNightlyPrice,
                                 @Nullable Integer minBeds,
                                 @Nullable Integer minBathrooms,
                                 @Nullable Integer minBedrooms,
                                 @NonNull Pageable pageable
                                 );

    @Query(value = "SELECT AVG(c.rating) AS averageRating, COUNT(*) AS totalRating " +
            "FROM comment c " +
            "WHERE c.property_id = :propertyId " +
            "GROUP BY c.property_id", nativeQuery = true)
    ReviewInfoProjection findAverageRatingForProperty(@Param("propertyId") Long propertyId);


    @NonNull
    @Query(value = """
        SELECT
            p.id AS id,
            p.nightly_price AS nightlyPrice,
            p.name AS name,
            p.longitude AS longitude,
            p.latitude AS latitude,
            p.created_at AS createdAt,
            p.updated_at AS updatedAt,
            p.num_beds AS numBeds,
            GROUP_CONCAT(DISTINCT i.id) AS imageIds,
            GROUP_CONCAT(DISTINCT i.name) AS imageNames,
            COALESCE(AVG(c.rating), 0) AS averageRating
        FROM property p
        LEFT JOIN image i ON p.id = i.property_id
        LEFT JOIN comment c ON p.id = c.property_id
        WHERE (:category1 IS NULL OR EXISTS 
            (SELECT 1 FROM property_categories pc1 WHERE pc1.property_id = p.id AND pc1.categories = :category1))
        AND (:category2 IS NULL OR EXISTS 
            (SELECT 1 FROM property_categories pc2 WHERE pc2.property_id = p.id AND pc2.categories = :category2))
        AND (:tag IS NULL OR p.tag = :tag)
        AND (:area IS NULL OR 
            (p.longitude BETWEEN :minLongitude AND :maxLongitude) AND
            (p.latitude BETWEEN :minLatitude AND :maxLatitude))
        AND (:minNightlyPrice IS NULL OR :maxNightlyPrice IS NULL OR
            p.nightly_price BETWEEN :minNightlyPrice AND :maxNightlyPrice)
        AND (:minBeds IS NULL OR p.num_beds >= :minBeds)
        AND (:minBathrooms IS NULL OR p.num_bathrooms >= :minBathrooms)
        AND (:minBedrooms IS NULL OR p.num_bedrooms >= :minBedrooms)
        GROUP BY p.id
        ORDER BY p.updated_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> findAllNative(@Nullable String category1,
                                 @Nullable String category2,
                                 @Nullable String tag,
                                 @Nullable Area area,
                                 @Nullable Double minLongitude,
                                 @Nullable Double maxLongitude,
                                 @Nullable Double minLatitude,
                                 @Nullable Double maxLatitude,
                                 @Nullable Double minNightlyPrice,
                                 @Nullable Double maxNightlyPrice,
                                 @Nullable Integer minBeds,
                                 @Nullable Integer minBathrooms,
                                 @Nullable Integer minBedrooms,
                                 @NonNull Long limit,
                                 @NonNull Long offset);
}
