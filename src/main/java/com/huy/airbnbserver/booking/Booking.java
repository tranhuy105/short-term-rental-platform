package com.huy.airbnbserver.booking;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.huy.airbnbserver.properties.Property;
import com.huy.airbnbserver.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "property_id"})})
public class Booking{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @Future
    private Date checkInDate;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @Future
    private Date checkOutDate;

    // confirm, pending, ...
    @Column(nullable = false)
    private boolean isConfirm = false;

    @Column(nullable = false, updatable = false)
    @Min(0)
    private Integer numAlduts;

    @Column(nullable = false, updatable = false)
    @Min(0)
    private Integer numChildrens;

    @Column(nullable = false, updatable = false)
    @Min(0)
    private Integer numPets;

    @Column(nullable = false, updatable = false)
    @Min(0)
    private  BigDecimal nightlyFee;

    @Column(nullable = false, updatable = false)
    @Min(0)
    private BigDecimal cleanFee;

    @Column(nullable = false, updatable = false)
    @Min(0)
    private BigDecimal serviceFee;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id")
    @JsonManagedReference
    private Property property;

    public void addUser(User user) {
        this.user = user;
        user.getBookings().add(this);
    }

    public void addProperty(Property property) {
        this.property = property;
        user.getBookings().add(this);
    }

    public void cancel() {
        this.user.getBookings().remove(this);
        this.property.getBookings().remove(this);
    }
}
