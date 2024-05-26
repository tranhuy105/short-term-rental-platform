package com.huy.airbnbserver.user;

import com.huy.airbnbserver.image.ImageService;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.user.dto.UserDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository
                .findByIdEager(id)
                .orElseThrow(()->new ObjectNotFoundException("user", id));
    }


    public User update(Integer userId, UserDto update){
        var oldUser = userRepository.findById(userId).orElseThrow(()->new ObjectNotFoundException("user", userId));
        oldUser.setFirstname(update.firstname());
        oldUser.setLastname(update.lastname());

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
