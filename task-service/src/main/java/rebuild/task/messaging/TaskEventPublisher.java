package rebuild.task.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import rebuild.task.event.TaskEvent;
import rebuild.task.model.Task;
import rebuild.task.model.TaskStatus;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
public class TaskEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TaskEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public TaskEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper,
                              @Value("${task.messaging.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public void publishStatusChanged(Task task) {
        String eventType = task.getStatus() == TaskStatus.COMPLETED ? "TaskCompleted" : "TaskStatusChanged";
        TaskEvent event = new TaskEvent(eventType, task.getId(), task.getProjectId(),
                task.getStatus().name(), task.getContractorId(), Instant.now());
        publish(event);
    }

    private void publish(TaskEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            String key = String.valueOf(event.projectId());

            kafkaTemplate.send(topic, key, json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent {} for task {} to topic {}", event.eventType(), event.taskId(), topic);
                        } else {
                            log.error("Could not send {} to Kafka: {}", event.eventType(), ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Could not send {} to Kafka: {}", event.eventType(), e.getMessage());
        }
    }
}