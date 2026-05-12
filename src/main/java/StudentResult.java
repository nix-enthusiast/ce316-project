import java.time.LocalDateTime;

public class StudentResult {
    private int id;
    private int projectId;
    private String studentId;
    private boolean compilationSuccess;
    private boolean runSuccess;
    private boolean outputMatch;
    private String errorMessage;
    private LocalDateTime runAt;

    public StudentResult(int projectId, String studentId, boolean compilationSuccess, boolean runSuccess, boolean outputMatch, String errorMessage) {
        this.projectId = projectId;
        this.studentId = studentId;
        this.compilationSuccess = compilationSuccess;
        this.runSuccess = runSuccess;
        this.outputMatch = outputMatch;
        this.errorMessage = errorMessage;
        this.runAt = LocalDateTime.now();
    }

    public StudentResult(int id, int projectId, String studentId, boolean compilationSuccess, boolean runSuccess, boolean outputMatch, String errorMessage, LocalDateTime runAt) {
        this.id = id;
        this.projectId = projectId;
        this.studentId = studentId;
        this.compilationSuccess = compilationSuccess;
        this.runSuccess = runSuccess;
        this.outputMatch = outputMatch;
        this.errorMessage = errorMessage;
        this.runAt = runAt;
    }

    // GETTERS
    public int getId() { return id; }
    public int getProjectId() { return projectId; }
    public String getStudentId() { return studentId; }
    public boolean isCompilationSuccess() { return compilationSuccess; }
    public boolean isRunSuccess() { return runSuccess; }
    public boolean isOutputMatch() { return outputMatch; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getRunAt() { return runAt; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setCompilationSuccess(boolean compilationSuccess) { this.compilationSuccess = compilationSuccess; }
    public void setRunSuccess(boolean runSuccess) { this.runSuccess = runSuccess; }
    public void setOutputMatch(boolean outputMatch) { this.outputMatch = outputMatch; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setRunAt(LocalDateTime runAt) { this.runAt = runAt; }
}
