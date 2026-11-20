package rebuild.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rebuild.task.model.Task;
import rebuild.task.model.TaskStatus;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByContractorId(Long contractorId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
}
