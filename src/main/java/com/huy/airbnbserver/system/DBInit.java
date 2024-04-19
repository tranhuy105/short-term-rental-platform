package com.huy.airbnbserver.system;

import com.huy.airbnbserver.user.User;
import com.huy.airbnbserver.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DBInit implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        var user = new User();
        user.setRoles("admin user");
        user.setUsername("tranhuy");
        user.setEmail("admin@test.com");
        user.setPassword("123");


        var user2 = new User();
        user2.setUsername("john");
        user2.setEmail("user@test.com");
        user2.setPassword("123");

        userService.save(user);
        userService.save(user2);
    }
}
