package com.flowpay.service;

import com.flowpay.dto.request.LoginRequest;
import com.flowpay.dto.request.RegisterRequest;
import com.flowpay.dto.response.LoginResponse;
import com.flowpay.dto.response.RegisterResponse;
import com.flowpay.exception.AppException;
import com.flowpay.exception.ErrorCode;
import com.flowpay.service.RefreshTokenService;
import com.flowpay.service.JwtService;
import com.flowpay.entity.UserEntity;
import com.flowpay.enums.UserRole;
import com.flowpay.enums.UserStatus;
import com.flowpay.repository.UserRepository;
import com.flowpay.entity.WalletEntity;
import com.flowpay.enums.CurrencyCode;
import com.flowpay.enums.WalletStatus;
import com.flowpay.enums.WalletType;
import com.flowpay.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String WALLET_PREFIX = "FW";

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = normalize(request.username());
        String email = normalize(request.email());

        validateUserNotExist(username, email);

        UserEntity user = createUser(request, username, email);

        UserEntity savedUser = userRepository.save(user);

        WalletEntity wallet = createWallet(savedUser);

        WalletEntity savedWallet = walletRepository.save(wallet);

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .userName(savedUser.getUsername())
                .walletNumber(savedWallet.getWalletNumber())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        String username = normalize(request.username());

        UserEntity user =
                userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        validatePassword(request.password(), user);
        validateUserStatus(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationSecond())
                .build();
    }

    private UserEntity createUser(RegisterRequest registerRequest, String username, String email){
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(registerRequest.fullName().trim());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        user.setUserRole(UserRole.USER);
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    private WalletEntity createWallet(UserEntity user) {
        WalletEntity wallet = new WalletEntity();
        wallet.setWalletNumber(generateWalletNumber(user.getId()));
        wallet.setOwner(user);
        wallet.setWalletType(WalletType.USER);
        wallet.setCurrency(CurrencyCode.VND);
        wallet.setStatus(WalletStatus.ACTIVE);

        return wallet;

    }

    private void validateUserNotExist(String username, String email) {
        if(userRepository.existsByUsernameIgnoreCase(username)){
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if(userRepository.existsByEmailIgnoreCase(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void validatePassword(String rawPassword, UserEntity user) {
        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPasswordHash());

        if(!passwordMatches) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    private void validateUserStatus(UserEntity user) {
        if(user.getUserStatus() == UserStatus.LOCKED) {
            throw new AppException(ErrorCode.USER_ACCOUNT_LOCKED);
        }
        if(user.getUserStatus() == UserStatus.DISABLED) {
            throw new AppException(ErrorCode.USER_ACCOUNT_DISABLED);
        }
    }

    private String generateWalletNumber(Long userId) {
        return WALLET_PREFIX + "%010d".formatted(userId);
    }
    private String normalize(String value) {
        return value.trim().toLowerCase();
    }
}
