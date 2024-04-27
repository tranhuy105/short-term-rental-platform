package com.huy.airbnbserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @NonNull
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@NonNull Integer id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.avatar WHERE u.id = :id")
    Optional<User> findByIdEager(@NonNull Integer id);
}
