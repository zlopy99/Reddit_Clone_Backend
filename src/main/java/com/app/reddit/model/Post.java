package com.app.reddit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.time.Instant;

import static javax.persistence.FetchType.LAZY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    @Nullable
    private String url;
    @Nullable
    @Lob
    private String description;
    private Integer voteCount;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subredditId", referencedColumnName = "id")
    private Subreddit subreddit;
    private Instant createdDate;
}
