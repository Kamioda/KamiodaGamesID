package com.kamioda.id.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kamioda.id.component.AppAuthorization;
import com.kamioda.id.component.AuthIDGenerator;
import com.kamioda.id.controller.ApplicationController;
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

    AuthorizationService(ApplicationController application) {
    }
    public String createAuthorization(String appId, String redirectURI, String codeChallenge, String codeChallengeMethod) {
        Optional<Application> application = applicationRepository.findById(appId);
        if (application.isEmpty() || !application.get().equalRedirectUri(redirectURI)) throw new NotFoundException("Application not found");
        String authId = authIDGenerator.generate();
        Authorization authorization = new Authorization(
            authId,
            redirectURI,
            codeChallenge,
            codeChallengeMethod,
            application.get()
        );
        authorizationRepository.save(authorization);
        return authId;
    }
    public String authorizationWithIDAndPassword(String authId, String id, String password) {
        Optional<Authorization> authIdRecord = authorizationRepository.findById("auth0-" + authId);
        if (authIdRecord.isEmpty()) throw new NotFoundException("Authorization not found");
        User user = userRepository.findByUserId(id);
        if (user == null || !User.hashPassword(password).equals(user.getPassword())) throw new UnauthorizationException("Invalid User ID or Password");
        String authCode = authIDGenerator.generate();
        Authorization auth = new Authorization(authIdRecord.get(), authCode, user);
        authorizationRepository.save(auth);
        authorizationRepository.deleteById(authId);
        return authCode;
    }
    public TokenDTO issueToken(String authCode, String codeVerifier, String redirectUri, AppAuthorization appAuthInfo) {
        Optional<Authorization> authorization = authorizationRepository.findById("auth1-" + authCode);
        if (authorization.isEmpty()) throw new NotFoundException("Authorization not found");
        Application app = authorization.get().getApp();
        if (app.getAppId() != appAuthInfo.getClientId()) throw new UnauthorizationException("Invalid application authorization");
        if (!authorization.get().verify(redirectUri, codeVerifier)) throw new UnauthorizationException("Invalid redirect URI or code verifier");
        if (!app.matchAppSecret(appAuthInfo.getClientSecret())) throw new UnauthorizationException("Invalid application authorization");
        return tokenService.createToken(authorization.get().getMasterID(), app);
    }
    public TokenDTO issueToken(String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }
}
