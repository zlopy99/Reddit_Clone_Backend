package com.app.reddit.repo;

import com.app.reddit.model.Post;
import com.app.reddit.model.User;
import com.app.reddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepo extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User user);
}
