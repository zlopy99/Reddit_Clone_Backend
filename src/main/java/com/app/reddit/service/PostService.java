package com.app.reddit.service;

import com.app.reddit.dto.PostRequest;
import com.app.reddit.dto.PostResponse;
import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.mapper.PostMapper;
import com.app.reddit.model.Post;
import com.app.reddit.model.Subreddit;
import com.app.reddit.model.User;
import com.app.reddit.repo.PostRepo;
import com.app.reddit.repo.SubredditRepo;
import com.app.reddit.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final PostRepo postRepo;
    private final SubredditRepo subredditRepo;
    private final UserRepo userRepo;
    private final AuthService authService;
    private final PostMapper postMapper;

    public List<PostResponse> getAllPosts() {
        return postRepo.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepo.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringRedditException(postRequest.getSubredditName()));
        postRepo.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
    }

    public PostResponse getPost(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new SpringRedditException("There is no Post with that id: " + id));
        return postMapper.mapToDto(post);
    }

    public List<PostResponse> getPostsBySubreddit(Long id) {
        Subreddit subreddit = subredditRepo.findById(id)
                .orElseThrow(() -> new SpringRedditException("There is no Subreddit with this id: " + id));
        List<Post> posts = postRepo.findBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User with the name: " + username + " does not exists"));
        List<Post> posts = postRepo.findByUser(user);
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }
}
