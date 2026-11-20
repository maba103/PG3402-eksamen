package rebuild.task.dto;

public record ProgressResponse (
        long projectId,
        long total,
        long planned,
        long inProgress,
        long completed,
        int percentageComplete
)

   {
}
