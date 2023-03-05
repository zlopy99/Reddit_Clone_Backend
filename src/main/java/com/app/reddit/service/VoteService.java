package com.app.reddit.service;

import com.app.reddit.dto.VoteDto;
import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.model.Post;
import com.app.reddit.model.Vote;
import com.app.reddit.repo.PostRepo;
import com.app.reddit.repo.VoteRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.app.reddit.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepo voteRepo;
    private final PostRepo postRepo;
    private final AuthService authService;
    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepo.findById(voteDto.getPostId())
                .orElseThrow(() -> new SpringRedditException("Post not found with ID -> " + voteDto.getPostId()));
        Optional<Vote> voteByPostAndUser = voteRepo.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        if (post.getVoteCount() == null) {
            post.setVoteCount(0);
        }
        if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post");
        }
        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepo.save(mapToVote(voteDto, post));
        postRepo.save(post);
    }

    private Vote mapToVote (VoteDto voteDto, Post post) {
        Vote vote = new Vote();
        vote.setVoteType(voteDto.getVoteType());
        vote.setPost(post);
        vote.setUser(authService.getCurrentUser());
        return vote;
    }
}
