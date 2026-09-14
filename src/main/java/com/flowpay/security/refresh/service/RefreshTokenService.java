package com.flowpay.security.refresh.service;


import com.flowpay.security.config.JwtProperties;
import com.flowpay.security.refresh.entity.RefreshTokenEntity;
import com.flowpay.security.refresh.enums.RefreshTokenStatus;
import com.flowpay.security.repository.RefreshTokenRepository;
import com.flowpay.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_SIZE_IN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken(UserEntity user) {
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setStatus(RefreshTokenStatus.ACTIVE);
        refreshToken.setExpiresAt(Instant.now().plus(jwtProperties.refreshTokenExpiration()));

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }


    private String generateRawToken() {
        byte[] randomBytes = new byte[TOKEN_SIZE_IN_BYTES];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] hash = messageDigest.digest(
                    rawToken.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
        throw new IllegalStateException("SHA-256 algorithm is not available");
        }
    }
}
