package com.kamioda.id.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kamioda.id.component.AppSecretGenerator;
import com.kamioda.id.model.Application;
import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.ApplicationCreateRequest;
import com.kamioda.id.model.dto.ApplicationCreateResponse;
import com.kamioda.id.model.dto.ApplicationInformation;
import com.kamioda.id.model.dto.ApplicationUpdateRequest;
import com.kamioda.id.repository.ApplicationRepository;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private AppSecretGenerator appSecretGenerator;
    @Autowired
    private TokenService tokenService;
    public ApplicationCreateResponse create(String accessToken, ApplicationCreateRequest request) throws BadRequestException {
        User developer = tokenService.getUser(accessToken);
        return create(developer, request);
    }
    public ApplicationCreateResponse create(User user, ApplicationCreateRequest request) throws BadRequestException {
        if (user.getId().charAt(0) != '0') throw new BadRequestException("This user cannot create applications");
        String appId = "app-" + UUID.randomUUID().toString().replace("-", "");
        String appSecret = appSecretGenerator.generate();
        Application application = new Application(appId, Application.hashAppSecret(appSecret), request.getName(), request.getDescription(), request.getRedirectUri(), user);
        applicationRepository.save(application);
        return new ApplicationCreateResponse(appId, appSecret);
    }
    public ApplicationCreateResponse update(String accessToken, String appId, ApplicationUpdateRequest request) throws BadRequestException {
        User developer = tokenService.getUser(accessToken);
        if (developer.getId().charAt(0) != '0') throw new BadRequestException("This user cannot update applications");
        Optional<Application> application = applicationRepository.findById(appId);
        if (application.isEmpty() || !application.get().matchDeveloper(developer.getId())) throw new BadRequestException("Application not found");
        applicationRepository.updateApplication(appId, null, request.getName(), request.getDescription(), request.getRedirectUri());
        if (request.getRegenerateClientSecret()) {
            String newAppSecret = appSecretGenerator.generate();
            applicationRepository.updateApplication(appId, Application.hashAppSecret(newAppSecret), null, null, null);
            return new ApplicationCreateResponse(appId, newAppSecret);
        }
        else return null;
    }
    public ApplicationInformation getApp(String applicationId) {
        return applicationRepository.findApplicationInformationById(applicationId);
    }
    public List<ApplicationInformation> getAppByDeveloper(String accessToken) throws BadRequestException {
        User developer = tokenService.getUser(accessToken);
        if (developer.getId().charAt(0) != '0') throw new BadRequestException("This user cannot access applications");
        return applicationRepository.findApplicationInformationByDeveloperId(developer.getId());
    }
    public void delete(String accessToken, String appId) throws BadRequestException {
        User developer = tokenService.getUser(accessToken);
        if (developer.getId().charAt(0) != '0') throw new BadRequestException("This user cannot delete applications");
        Optional<Application> application = applicationRepository.findById(appId);
        if (application.isEmpty() || !application.get().matchDeveloper(developer.getId())) throw new BadRequestException("Application not found");
        applicationRepository.deleteById(appId);
    }
}
