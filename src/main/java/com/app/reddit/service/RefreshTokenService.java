package com.app.reddit.service;

import com.app.reddit.exception.SpringRedditException;
import com.app.reddit.model.RefreshToken;
import com.app.reddit.repo.RefreshTokenRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepo.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new SpringRedditException("Invalid refresh Token!"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepo.deleteByToken(token);
    }
}
