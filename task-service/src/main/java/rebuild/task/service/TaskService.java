package rebuild.task.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rebuild.task.client.ProjectClient;
import rebuild.task.dto.TaskRequest;
import rebuild.task.dto.TaskResponse;
import rebuild.task.exception.InvalidTaskException;
import rebuild.task.exception.TaskNotFoundException;
import rebuild.task.model.Task;
import rebuild.task.model.TaskStatus;
import rebuild.task.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final ProjectClient projectClient;

    public TaskService(TaskRepository repository, ProjectClient projectClient) {
        this.repository = repository;
        this.projectClient = projectClient;
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

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        Task task = getTask(id);
        task.setStatus(status);
        return TaskResponse.from(repository.save(task));
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