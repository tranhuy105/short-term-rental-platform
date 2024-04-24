package com.huy.airbnbserver.properties;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.huy.airbnbserver.booking.Booking;
import com.huy.airbnbserver.image.Image;
import com.huy.airbnbserver.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Property implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull @Min(0)
    private BigDecimal nightlyPrice;

    @Column(nullable = false, length = 200)
    @NotEmpty
    private String name;

    @Column(nullable = false)
    @NotNull @Min(0)
    private Integer maxGuests;

    @Column(nullable = false)
    @NotNull @Min(0)
    private Integer numBeds;

    @Column(nullable = false)
    @NotNull @Min(0)
    private Integer numBedrooms;

    @Column(nullable = false)
    @NotNull @Min(0)
    private Integer numBathrooms;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    private BigDecimal longitude;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    private BigDecimal latitude;

    @Column(nullable = false, length = 2000)
    @NotEmpty
    private String description;

    @Column(nullable = false, length = 500)
    @NotEmpty
    private String addressLine;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    // can be null
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    // relationship

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    @JsonManagedReference
    private User host;

    @ManyToMany(mappedBy = "likedProperty", fetch = FetchType.LAZY)
    private List<User> likedByUsers = new ArrayList<>();

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL) @JsonBackReference
    private List<Booking> bookings = new ArrayList<>();

    public void addImages(Image image) {
        image.setProperty(this);
        this.images.add(image);
    }

    public void addLikedUser(User user) {
        user.getLikedProperty().add(this);
        this.likedByUsers.add(user);
    }

    public void removeLikedUser(User user) {
        user.getLikedProperty().remove(this);
        this.likedByUsers.remove(user);
    }
}
