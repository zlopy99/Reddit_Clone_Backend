package com.app.reddit.service;

import com.app.reddit.dto.SubRedditDto;
import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.mapper.SubredditMapper;
import com.app.reddit.model.Subreddit;
import com.app.reddit.repo.SubredditRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubRedditService {
    private final SubredditRepo subredditRepo;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubRedditDto save(SubRedditDto subRedditDto) {
        Subreddit save = subredditRepo.save(subredditMapper.mapDtoToSubreddit(subRedditDto));
        subRedditDto.setId(save.getSubredditId());
        return subRedditDto;
    }

    @Transactional
    public List<SubRedditDto> getAll() {
        return subredditRepo.findAll()
                .stream()
                .map(subredditMapper::mapSubRedditDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubRedditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepo.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return subredditMapper.mapSubRedditDto(subreddit);
    }


}
