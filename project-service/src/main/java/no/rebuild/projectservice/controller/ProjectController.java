package no.rebuild.projectservice.controller;

import no.rebuild.projectservice.dto.ProjectSummaryResponse;
import jakarta.validation.Valid;
import no.rebuild.projectservice.dto.ProjectRequest;
import no.rebuild.projectservice.dto.ProjectResponse;
import no.rebuild.projectservice.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse created = projectService.createProject(request);
        return ResponseEntity
                .created(URI.create("/api/projects/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<ProjectResponse> getProjects(@RequestParam(required = false) Long ownerId) {
        return projectService.getProjects(ownerId);
    }

    @GetMapping("/{id}/summary")
    public ProjectSummaryResponse getProjectSummary(@PathVariable Long id) {
        return projectService.getProjectSummary(id);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(@PathVariable Long id,
                                         @Valid @RequestBody ProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}