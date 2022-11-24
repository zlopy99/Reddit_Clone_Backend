package com.app.reddit.repo;

import com.app.reddit.model.Comment;
import com.app.reddit.model.Post;
import com.app.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByUser(User user);
}
