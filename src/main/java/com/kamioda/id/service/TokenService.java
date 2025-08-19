package com.kamioda.id.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kamioda.id.component.TokenGenerator;
import com.kamioda.id.exception.UnauthorizationException;
import com.kamioda.id.model.Application;
import com.kamioda.id.model.Token;
import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.TokenDTO;
import com.kamioda.id.repository.TokenRepository;
import com.kamioda.id.repository.UserRepository;

@Component
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TokenGenerator tokenGenerator;
    @Autowired
    private UserRepository userRepository;
    public TokenDTO createToken(String id, Application app) {
        String accessToken = tokenGenerator.generate(128);
        String refreshToken = tokenGenerator.generate(128);
        if (tokenRepository.findByAccessToken(Token.hashAccessToken(accessToken)) != null
            || tokenRepository.findByRefreshToken(Token.hashRefreshToken(refreshToken)) != null) return createToken(id, app);
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty() || app == null) throw new IllegalArgumentException("Invalid User ID or Application ID");
        Token token = new Token(accessToken, refreshToken, user.get(), app);
        tokenRepository.save(token);
        token = tokenRepository.findByAccessToken(Token.hashAccessToken(accessToken));
        return new TokenDTO(accessToken, refreshToken, token.getCreatedAt());
    }
    public TokenDTO refreshToken(String refreshToken) {
        String hashedRefreshToken = Token.hashRefreshToken(refreshToken);
        Token token = tokenRepository.findByRefreshToken(hashedRefreshToken);
        if (token == null || token.refreshTokenExpired()) throw new UnauthorizationException("Invalid Refresh Token");
        tokenRepository.deleteByRefreshToken(hashedRefreshToken);
        return createToken(token.getUserMasterID(), token.getApplication());
    }
    public User getUser(String accessToken) {
        Token token = tokenRepository.findByAccessToken(Token.hashAccessToken(accessToken));
        if (token == null || token.accessTokenExpired()) throw new UnauthorizationException("Invalid Access Token");
        return token.getUser();
    }
    public void revokeToken(String accessToken) {
        String hashedAccessToken = Token.hashAccessToken(accessToken);
        Token token = tokenRepository.findByAccessToken(hashedAccessToken);
        if (token == null) throw new UnauthorizationException("Invalid Access Token");
        tokenRepository.deleteByAccessToken(hashedAccessToken);
    }
}