package com.example.budgetservice.client;

import com.example.budgetservice.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


@Component
public class ProjectClient {

    private static final Logger log = LoggerFactory.getLogger(ProjectClient.class);

    private final RestClient restClient;
    private final String projectPath;
    private final boolean validationEnabled;

    public ProjectClient(RestClient.Builder loadBalancedRestClientBuilder,
                         @Value("${budget.project-service.base-url}") String baseUrl,
                         @Value("${budget.project-service.project-path}") String projectPath,
                         @Value("${budget.project-service.validation-enabled}") boolean validationEnabled) {
        this.restClient = loadBalancedRestClientBuilder.baseUrl(baseUrl).build();
        this.projectPath = projectPath;
        this.validationEnabled = validationEnabled;
    }

    public boolean projectExists(Long projectId) {
        if (!validationEnabled) {
            log.debug("Project validation is turned off, assuming project {} exists", projectId);
            return true;
        }
        try {
            restClient.get()
                    .uri(projectPath, projectId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RuntimeException e) {
            log.warn("Could not reach project-service for project {}: {}", projectId, e.getMessage());
            throw new ServiceUnavailableException(
                    "project-service is not available, could not check project " + projectId);
        }
    }
}