package com.huy.airbnbserver.system;


import com.huy.airbnbserver.properties.PropertyService;
import com.huy.airbnbserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DBInit implements CommandLineRunner {
    private final UserService userService;
    private final PropertyService propertyService;

    @Override
    public void run(String... args) throws Exception {


//        var property = Property.builder()
//                .nightlyPrice(BigDecimal.valueOf(99.99))
//                .name("Khach San Ho Tay")
//                .maxGuests(4)
//                .numBathrooms(2)
//                .numBedrooms(2)
//                .numBeds(3)
//                .longitude(BigDecimal.valueOf(12.43))
//                .latitude(BigDecimal.valueOf(43.3))
//                .description("A super beautiful hotel with the view of West Lake")
//                .addressLine("27 West Lake")
//                .images(new ArrayList<>())
//                .likedByUsers(new ArrayList<>())
//                .bookings(new ArrayList<>())
//                .host(user)
//                .build();


    }
}
