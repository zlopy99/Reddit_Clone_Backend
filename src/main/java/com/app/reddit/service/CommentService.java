package com.app.reddit.service;

import com.app.reddit.dto.CommentsDto;
import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.mapper.CommentMapper;
import com.app.reddit.model.Comment;
import com.app.reddit.model.NotificationEmail;
import com.app.reddit.model.Post;
import com.app.reddit.model.User;
import com.app.reddit.repo.CommentRepo;
import com.app.reddit.repo.PostRepo;
import com.app.reddit.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private static final String POST_URL = "";
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepo commentRepo;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentsDto commentsDto) {
        Post post = postRepo.findById(commentsDto.getPostId())
                .orElseThrow(() -> new SpringRedditException(commentsDto.getPostId().toString()));
        Comment map = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepo.save(map);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendEmail(new NotificationEmail(user.getUsername() + " commented on your post", user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepo.findById(postId).
                orElseThrow(() -> new SpringRedditException("There is no post with Id " + postId.toString()));
        List<Comment> comments = commentRepo.findByPost(post);
        List<CommentsDto> commentsDto = comments.stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
        return commentsDto;
    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user = userRepo.findByUsername(userName)
                .orElseThrow(() -> new SpringRedditException("User with the name " + userName.toString() + " does not exists."));
        return commentRepo.findByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
