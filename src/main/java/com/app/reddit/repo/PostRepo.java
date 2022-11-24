package com.app.reddit.repo;

import com.app.reddit.model.Post;
import com.app.reddit.model.Subreddit;
import com.app.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findBySubreddit(Subreddit subreddit);
    List<Post> findByUser(User user);
}
