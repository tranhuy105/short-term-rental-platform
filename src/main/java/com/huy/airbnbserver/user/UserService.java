package com.huy.airbnbserver.user;

import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.system.exception.UserAlreadyExistException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(()->new ObjectNotFoundException("user", id));
    }

    public User save(User user){
        var userCheck = userRepository.findByEmail(user.getEmail());
        if (userCheck.isPresent()) {
            throw new UserAlreadyExistException();
        }
        user.setRoles("user");
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Integer userId, User update){
        var oldUser = userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        oldUser.setUsername(update.getUsername());
        oldUser.setEmail(update.getEmail());

        return userRepository.save(oldUser);
    }

    public void delete(Integer id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("user", id));
        userRepository.deleteById(id);
    }

    @Override
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("email: " + email + " not found"));
    }
}
