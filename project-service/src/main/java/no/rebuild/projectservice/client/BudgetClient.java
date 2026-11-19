package no.rebuild.projectservice.client;

import no.rebuild.projectservice.dto.BudgetInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BudgetClient {

    private final RestClient restClient;

    public BudgetClient(RestClient.Builder loadBalancedRestClientBuilder,
                        @Value("${project.budget-service.base-url:http://budget-service}") String baseUrl) {
        this.restClient = loadBalancedRestClientBuilder.baseUrl(baseUrl).build();
    }

    public BudgetInfo getBudgetForProject(Long projectId) {
        return restClient.get()
                .uri("/api/budgets/project/{projectId}", projectId)
                .retrieve()
                .body(BudgetInfo.class);
    }
}
