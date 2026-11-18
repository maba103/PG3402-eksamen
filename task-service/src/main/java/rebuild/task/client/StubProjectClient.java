package rebuild.task.client;

import org.springframework.stereotype.Component;

@Component
public class StubProjectClient implements ProjectClient {

    @Override
    public boolean projectExists(Long projectId) {
        return true;
    }
}