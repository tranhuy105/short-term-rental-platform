package com.huy.airbnbserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM user_account u WHERE u.email = :email", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @NonNull
    @Query(value = "SELECT * FROM user_account u WHERE u.id = :id", nativeQuery = true)
    Optional<User> findById(@NonNull Integer id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.avatar WHERE u.id = :id")
    Optional<User> findByIdEager(@NonNull Integer id);
}
