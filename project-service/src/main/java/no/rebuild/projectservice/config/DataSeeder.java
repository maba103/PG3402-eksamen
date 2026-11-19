package no.rebuild.projectservice.config;

import no.rebuild.projectservice.model.Project;
import no.rebuild.projectservice.model.ProjectStatus;
import no.rebuild.projectservice.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProjectRepository projectRepository;
    private final boolean seedEnabled;

    public DataSeeder(ProjectRepository projectRepository,
                      @Value("${project.seed.enabled:true}") boolean seedEnabled) {
        this.projectRepository = projectRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Seeding is turned off");
            return;
        }
        if (projectRepository.count() > 0) {
            log.info("Projects already exist, skipping seed data");
            return;
        }

        projectRepository.saveAll(List.of(
                project("Bathroom renovation", "New tiles, plumbing and shower",
                        "Storgata 1, Oslo", 1L, "150000", ProjectStatus.IN_PROGRESS,
                        LocalDate.of(2026, 10, 15), LocalDate.of(2026, 12, 20)),
                project("Kitchen renovation", "New cabinets and electrical work",
                        "Storgata 1, Oslo", 1L, "50000", ProjectStatus.IN_PROGRESS,
                        LocalDate.of(2026, 11, 1), LocalDate.of(2027, 1, 15)),
                project("Terrace", "New wooden terrace with railing",
                        "Bygdøy allé 12, Oslo", 2L, "60000", ProjectStatus.PLANNED,
                        LocalDate.of(2027, 4, 1), LocalDate.of(2027, 5, 15))
        ));

        log.info("Seeded {} projects", projectRepository.count());
    }

    private Project project(String name, String description, String address, Long ownerId,
                            String budgetLimit, ProjectStatus status,
                            LocalDate startDate, LocalDate endDate) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setAddress(address);
        project.setOwnerId(ownerId);
        project.setBudgetLimit(new BigDecimal(budgetLimit));
        project.setStatus(status);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        return project;
    }
}