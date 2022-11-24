package com.app.reddit.service;

import com.app.reddit.dto.SubRedditDto;
import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.model.Subreddit;
import com.app.reddit.repo.SubredditRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubRedditService {
    private final SubredditRepo subredditRepo;
    private final AuthService authService;

    @Transactional
    public SubRedditDto save(SubRedditDto subRedditDto) {
        Subreddit save = subredditRepo.save(mapSubRedditDto(subRedditDto));
        subRedditDto.setId(save.getId());
        return subRedditDto;
    }

    @Transactional
    public List<SubRedditDto> getAll() {
        return subredditRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubRedditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepo.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return mapToDto(subreddit);
    }

    private SubRedditDto mapToDto(Subreddit subreddit) {
        return SubRedditDto.builder().name(subreddit.getName())
                .id(subreddit.getId())
                .numberOfPosts(subreddit.getPosts().size())
                .build();
    }

    private Subreddit mapSubRedditDto(SubRedditDto subRedditDto) {
        return Subreddit.builder().name(subRedditDto.getName())
                .description(subRedditDto.getDescription())
                .user(authService.getCurrentUser())
                .createdDate(Instant.now())
                .build();
    }
}
