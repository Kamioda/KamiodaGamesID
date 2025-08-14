package com.kamioda.id.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kamioda.id.component.AuthIDGenerator;
import com.kamioda.id.exception.NotFoundException;
import com.kamioda.id.exception.UnauthorizationException;
import com.kamioda.id.model.Application;
import com.kamioda.id.model.Authorization;
import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.TokenDTO;
import com.kamioda.id.repository.ApplicationRepository;
import com.kamioda.id.repository.AuthorizationRepository;
import com.kamioda.id.repository.UserRepository;

@Service
public class AuthorizationService {
    @Autowired
    private AuthorizationRepository authorizationRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private AuthIDGenerator authIDGenerator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    public String createAuthorization(String appId, String redirectURI, String codeChallenge, String codeChallengeMethod) {
        Optional<Application> application = applicationRepository.findById(appId);
        if (application.isEmpty() || !application.get().equalRedirectUri(redirectURI)) throw new NotFoundException("Application not found");
        String authId = authIDGenerator.generate();
        authorizationRepository.createAuthorization(authId, appId, redirectURI, codeChallenge, codeChallengeMethod);
        return authId;
    }
    public String authorizationWithIDAndPassword(String authId, String id, String password) {
        Authorization authorization = authorizationRepository.findByAuthId(authId);
        if (authorization == null) throw new NotFoundException("Authorization not found");
        User user = userRepository.findByUserId(id);
        if (user == null || !User.hashPassword(password).equals(user.getPassword())) throw new UnauthorizationException("Invalid User ID or Password");
        String authCode = authIDGenerator.generate();
        authorizationRepository.updateToAuthCode(authCode, user.getId(), authId);
        return authorization.getRedirectUri(authCode);
    }
    public TokenDTO issueToken(String authCode, String codeVerifier, String redirectUri) {
        Authorization authorization = authorizationRepository.findByAuthCode(authCode);
        if (authorization == null || !authorization.verify(redirectUri, codeVerifier)) throw new UnauthorizationException("Invalid redirect URI or code verifier");
        return tokenService.createToken(authorization.getMasterID(), authorization.getAppID());
    }
    public TokenDTO issueToken(String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }
}
