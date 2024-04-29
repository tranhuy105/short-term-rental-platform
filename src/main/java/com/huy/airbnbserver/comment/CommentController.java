package com.huy.airbnbserver.comment;

import com.huy.airbnbserver.comment.converter.CommentDtoToCommentConverter;
import com.huy.airbnbserver.comment.converter.CommentToCommentDtoConverter;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.SortDirection;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.system.Utils;
import com.huy.airbnbserver.system.exception.InvalidSearchQueryException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;
    private final CommentDtoToCommentConverter commentDtoToCommentConverter;
    private final CommentToCommentDtoConverter commentToCommentDtoConverter;

    @GetMapping("/properties/{propertyId}/comments")
    public Result fetchAll(
            @PathVariable Long propertyId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_direction", required = false) String sortDirectionParam
    ) {
        if (page != null && page < 1) {
            throw new InvalidSearchQueryException("Page must be greater than zero");
        }

        if (pageSize != null && pageSize < 5) {
            throw new InvalidSearchQueryException("Page size must be at least 5");
        }

        SortDirection sortDirection;
        if (sortDirectionParam == null) {
            sortDirection = SortDirection.DESC;
        } else {
            try {
                sortDirection = SortDirection.valueOf(sortDirectionParam.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new InvalidSearchQueryException("Sort direction can only be either 'asc' or 'desc'");
            }
        }

        List<CommentDto> commentDtoList = commentService.findByPropertyId(propertyId, page, pageSize, sortDirection)
                .stream()
                .map(commentToCommentDtoConverter::convert)
                .toList();
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch Comments Success",
                commentDtoList
        );
    }

    @PostMapping("/properties/{propertyId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Result newComment(
            @PathVariable Long propertyId,
            @Valid @RequestBody CommentDto commentDto,
            Authentication authentication
    ) {
        return new Result(
                true,
                StatusCode.CREATED,
                "Add new comment success",
                commentToCommentDtoConverter.convert(commentService.addComment(
                        Objects.requireNonNull(commentDtoToCommentConverter.convert(commentDto)),
                        propertyId,
                        Utils.extractAuthenticationId(authentication)
                ))
        );
    }

    @PutMapping("/comments/{commentId}")
    public Result updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto commentDto,
            Authentication authentication
    ) {
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Update comment success",
                commentToCommentDtoConverter
                        .convert(commentService.updateComment(
                                commentDtoToCommentConverter.convert(commentDto),
                                commentId,
                                Utils.extractAuthenticationId(authentication)
                        ))
        );
    }

    @DeleteMapping("/comments/{commentId}")
    public Result deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        commentService.delete(commentId, Utils.extractAuthenticationId(authentication));
        return new Result(true, 200, "Deleted Success");
    }

    @GetMapping("/comments/test")
    public Result test() {
        return new Result(true, 200, "ok");
    }
}
