package no.rebuild.projectservice.service;

import no.rebuild.projectservice.client.BudgetClient;
import no.rebuild.projectservice.dto.BudgetAvailability;
import no.rebuild.projectservice.dto.BudgetInfo;
import no.rebuild.projectservice.dto.ProjectSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
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

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final BudgetClient budgetClient;

    public ProjectService(ProjectRepository projectRepository, BudgetClient budgetClient) {
        this.projectRepository = projectRepository;
        this.budgetClient = budgetClient;
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

    public ProjectSummaryResponse getProjectSummary(Long id) {
        ProjectResponse project = ProjectResponse.from(findProject(id));

        try {
            BudgetInfo budget = budgetClient.getBudgetForProject(id);
            return new ProjectSummaryResponse(project, BudgetAvailability.AVAILABLE, budget);
        } catch (HttpClientErrorException.NotFound e) {
            return new ProjectSummaryResponse(project, BudgetAvailability.NOT_REGISTERED, null);
        } catch (RuntimeException e) {
            log.warn("Could not reach budget-service for project {}: {}", id, e.getMessage());
            return new ProjectSummaryResponse(project, BudgetAvailability.SERVICE_UNAVAILABLE, null);
        }
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