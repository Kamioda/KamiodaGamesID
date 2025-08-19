package com.kamioda.id.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kamioda.id.component.TokenGenerator;
import com.kamioda.id.exception.UnauthorizationException;
import com.kamioda.id.model.Application;
import com.kamioda.id.model.Token;
import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.TokenDTO;
import com.kamioda.id.repository.TokenRepository;

@Component
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TokenGenerator tokenGenerator;
    public TokenDTO createToken(User user, Application app) {
        String accessToken = tokenGenerator.generate(128);
        String refreshToken = tokenGenerator.generate(128);
        if (tokenRepository.findByAccessToken(Token.hashAccessToken(accessToken)) != null
            || tokenRepository.findByRefreshToken(Token.hashRefreshToken(refreshToken)) != null) return createToken(user, app);
        Token token = new Token(accessToken, refreshToken, user, app);
        tokenRepository.save(token);
        token = tokenRepository.findByAccessToken(Token.hashAccessToken(accessToken));
        return new TokenDTO(accessToken, refreshToken, token.getCreatedAt());
    }
    public TokenDTO refreshToken(String refreshToken) {
        String hashedRefreshToken = Token.hashRefreshToken(refreshToken);
        Token token = tokenRepository.findByRefreshToken(hashedRefreshToken);
        if (token == null || token.refreshTokenExpired()) throw new UnauthorizationException("Invalid Refresh Token");
        tokenRepository.deleteByRefreshToken(hashedRefreshToken);
        return createToken(token.getUser(), token.getApplication());
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