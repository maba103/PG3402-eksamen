package rebuild.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rebuild.task.dto.TaskRequest;
import rebuild.task.dto.TaskResponse;
import rebuild.task.model.TaskStatus;
import rebuild.task.service.TaskService;

import java.util.List;

@Tag(name = "Tasks", description = "Operations for renovation tasks")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Create a task for a project")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return service.create(request);
    }

    @Operation(summary = "List tasks, optionally filtered by project or contractor")
    @GetMapping
    public List<TaskResponse> list(@RequestParam(required = false) Long projectId,
                                   @RequestParam(required = false) Long contractorId) {
        return service.findAll(projectId, contractorId);
    }

    @Operation(summary = "Get a task by id")
    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Update the status of a task")
    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        return service.updateStatus(id, status);
    }

    @Operation(summary = "Assign a contractor to a task")
    @PatchMapping("/{id}/contractor")
    public TaskResponse assignContractor(@PathVariable Long id, @RequestParam Long contractorId) {
        return service.assignContractor(id, contractorId);
    }
}