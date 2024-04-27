package com.huy.airbnbserver.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query( "SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user u " +
            "lEFT JOIN FETCH u.avatar " +
            "WHERE c.property.id = :propertyId")
    List<Comment> findAllByPropertyIdWithEagerFetching(Long propertyId);

    @NonNull
    @Query(value = "SELECT c.* FROM comment c WHERE c.id = :id", nativeQuery = true)
    Optional<Comment> findByIdLazy(@NonNull Long id);

    @Query(value = "SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user u " +
            "LEFT JOIN FETCH u.avatar " +
            "WHERE c.id = :id")
    Optional<Comment> findByIdEager(@NonNull Long id);
}
