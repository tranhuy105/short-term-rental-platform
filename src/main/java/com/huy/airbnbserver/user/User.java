package com.huy.airbnbserver.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "USER_ACCOUNT", indexes = {
        @Index(name = "email_index", columnList = "email")
})
public class User implements Serializable {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotEmpty(message = "username is required")
    private String username;

    @Column(nullable = false, unique = true, length = 200)
    @NotEmpty(message = "email is required")
    private String email;

    @Column(nullable = false, length = 200)
    @NotEmpty
    private String password;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    // can be null
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @Column(updatable = false, nullable = false)
    private boolean enabled = true;

    @Column(updatable = false, nullable = false, length = 100)
    private String roles = "user";
}