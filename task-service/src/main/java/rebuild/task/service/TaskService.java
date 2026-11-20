package rebuild.task.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rebuild.task.client.ProjectClient;
import rebuild.task.dto.TaskRequest;
import rebuild.task.dto.TaskResponse;
import rebuild.task.exception.InvalidTaskException;
import rebuild.task.exception.TaskNotFoundException;
import rebuild.task.messaging.TaskEventPublisher;
import rebuild.task.model.Task;
import rebuild.task.model.TaskStatus;
import rebuild.task.repository.TaskRepository;
import rebuild.task.dto.ProgressResponse;
import rebuild.task.exception.ContractorNotAssignedException;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final ProjectClient projectClient;
    private final TaskEventPublisher eventPublisher;


    public TaskService(TaskRepository repository, ProjectClient projectClient,TaskEventPublisher eventPublisher) {
        this.repository = repository;
        this.projectClient = projectClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        if (!projectClient.projectExists(request.projectId())) {
            throw new InvalidTaskException("Project does not exist: " + request.projectId());
        }
        Task task = new Task();
        task.setProjectId(request.projectId());
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDeadline(request.deadline());
        task.setStatus(TaskStatus.PLANNED);
        return TaskResponse.from(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll(Long projectId, Long contractorId) {
        List<Task> tasks;
        if (projectId != null) {
            tasks = repository.findByProjectId(projectId);
        } else if (contractorId != null) {
            tasks = repository.findByContractorId(contractorId);
        } else {
            tasks = repository.findAll();
        }
        return tasks.stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return TaskResponse.from(getTask(id));
    }


    @Transactional(readOnly = true)
    public ProgressResponse getProgress(Long projectId) {
        long total = repository.countByProjectId(projectId);

        long planned = repository.countByProjectIdAndStatus(projectId, TaskStatus.PLANNED);
        long inProgress = repository.countByProjectIdAndStatus(projectId, TaskStatus.IN_PROGRESS);
        long completed = repository.countByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);

        int percentage = 0;

        if (total > 0) {
            percentage = (int) Math.round((completed * 100.0) / total);
        }

        return new ProgressResponse(
                projectId,
                total,
                planned,
                inProgress,
                completed,
                percentage
        );
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status, Long contractorId) {
        Task task = getTask(id);
        if (task.getContractorId() == null || !task.getContractorId().equals(contractorId)) {
            throw new ContractorNotAssignedException(id, contractorId);

        }
        task.setStatus(status);
        Task saved = repository.save(task);
        eventPublisher.publishStatusChanged(saved);
        return TaskResponse.from(saved);

    }

    @Transactional
    public TaskResponse assignContractor(Long id, Long contractorId) {
        Task task = getTask(id);
        task.setContractorId(contractorId);
        return TaskResponse.from(repository.save(task));
    }

    private Task getTask(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}