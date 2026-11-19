package rebuild.task.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "project.validation.enabled", havingValue = "false", matchIfMissing = true)
public class StubProjectClient implements ProjectClient {

    @Override
    public boolean projectExists(Long projectId) {
        return true;
    }
}