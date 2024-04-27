package com.huy.airbnbserver.comment;

import com.huy.airbnbserver.properties.Property;
import com.huy.airbnbserver.properties.PropertyRepository;
import com.huy.airbnbserver.properties.PropertyService;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.user.User;
import com.huy.airbnbserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PropertyService propertyService;
    private final UserService userService;

    public Comment addComment(Comment comment, Long propertyId, Integer userId) {
        Property property = propertyService.findById(propertyId);
        User user = userService.findById(userId);
        comment.addProperty(property);
        comment.addUser(user);
        return commentRepository.save(comment);
    }

    public List<Comment> findByPropertyId(Long propertyId) {
        return commentRepository.findAllByPropertyIdWithEagerFetching(propertyId);
    }

    public void delete(Long commentId, Integer userId) {
        var deletedComment = commentRepository.findByIdEager(commentId).orElseThrow(
                () -> new ObjectNotFoundException("comment", commentId)
        );
        if (!deletedComment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied For This User");
        }

        commentRepository.delete(deletedComment);
    }

    public Comment updateComment(Comment comment,Long commentId, Integer userId) {
        var updatedComment = commentRepository.findByIdEager(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("comment", commentId));
        if (!updatedComment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied For This User");
        }

        updatedComment.setContent(comment.getContent());
        updatedComment.setRating(comment.getRating());

        return commentRepository.save(updatedComment);
    }
}
