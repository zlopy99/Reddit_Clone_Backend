package com.app.reddit.mapper;

import com.app.reddit.dto.SubRedditDto;
import com.app.reddit.model.Post;
import com.app.reddit.model.Subreddit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "mapSubRedditDto.numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubRedditDto mapSubRedditDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubRedditDto subRedditDto);
}
