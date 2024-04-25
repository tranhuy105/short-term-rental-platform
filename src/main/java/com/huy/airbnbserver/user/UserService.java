package com.huy.airbnbserver.user;

import com.huy.airbnbserver.image.ImageService;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.system.exception.EntityAlreadyExistException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;
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
            throw new EntityAlreadyExistException("user");
        }
//        user.setRoles("user");
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Integer userId, User update){
        var oldUser = userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        oldUser.setFirstname(update.getFirstname());
        oldUser.setLastname(update.getLastname());
        oldUser.setEmail(update.getEmail());

        return userRepository.save(oldUser);
    }

    public void delete(Integer id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("user", id));
        userRepository.deleteById(id);
    }



    @Transactional
    public void assignAvatar(Integer id, List<MultipartFile> files) throws IOException {
        var user = userRepository.findById(id).orElseThrow(()->new ObjectNotFoundException("user", id));
        // remove current image
        if (user.getAvatar() != null) {
            imageService.deleteById(user.getAvatar().getId());
        }
        var images = imageService.upload(files);
        user.setAvatar(images.get(0));
        userRepository.save(user);
    }
}
