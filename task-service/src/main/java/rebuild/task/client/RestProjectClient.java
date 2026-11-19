package rebuild.task.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import rebuild.task.exception.ProjectServiceUnavailableException;

@Component
@ConditionalOnProperty(name = "project.validation.enabled", havingValue = "true")
public class RestProjectClient implements ProjectClient {

    private final RestClient restClient;

    public RestProjectClient(@Value("${project.service.url}") String baseUrl) {
        this.restClient = RestClient.create(baseUrl);
    }

    @Override
    public boolean projectExists(Long projectId) {
        try {
            return restClient.get()
                    .uri("/api/projects/{id}", projectId)
                    .exchange((request, response) -> response.getStatusCode().is2xxSuccessful());
        } catch (RestClientException e) {
            throw new ProjectServiceUnavailableException("project-service is not reachable");
        }
    }
}