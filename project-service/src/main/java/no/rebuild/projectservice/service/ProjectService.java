package no.rebuild.projectservice.service;

import no.rebuild.projectservice.dto.ProjectRequest;
import no.rebuild.projectservice.dto.ProjectResponse;
import no.rebuild.projectservice.exception.InvalidProjectException;
import no.rebuild.projectservice.exception.ProjectNotFoundException;
import no.rebuild.projectservice.model.Project;
import no.rebuild.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        validateDates(request);
        Project project = new Project();
        applyRequest(project, request);
        Project saved = projectRepository.save(project);
        return ProjectResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(Long ownerId) {
        List<Project> projects = (ownerId == null)
                ? projectRepository.findAll()
                : projectRepository.findByOwnerId(ownerId);

        return projects.stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        return ProjectResponse.from(findProject(id));
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        validateDates(request);
        Project project = findProject(id);
        applyRequest(project, request);
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = findProject(id);
        projectRepository.delete(project);
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    private void applyRequest(Project project, ProjectRequest request) {
        project.setName(request.name());
        project.setDescription(request.description());
        project.setAddress(request.address());
        project.setOwnerId(request.ownerId());
        project.setBudgetLimit(request.budgetLimit());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        if (request.status() != null) {
            project.setStatus(request.status());
        }
    }

    private void validateDates(ProjectRequest request) {
        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new InvalidProjectException("End date cannot be before start date");
        }
    }
}