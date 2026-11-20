package rebuild.task.exception;

public class ContractorNotAssignedException extends RuntimeException {
    public ContractorNotAssignedException(Long taskId,Long contractorId) {
        super("Task" + taskId + "is not assigned to contractor" + contractorId);
    }
}
