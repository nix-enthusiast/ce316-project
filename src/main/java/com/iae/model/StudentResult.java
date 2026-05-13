package com.iae.model;

public class StudentResult {

    private int id;
    private int projectId;
    private String studentId;
    private boolean compilationSuccess;
    private boolean executionSuccess;
    private boolean outputMatch;
    private String compilationLog;
    private String executionLog;
    private String errorLog;

    public StudentResult(int projectId, String studentId, boolean compilationSuccess,
                         boolean executionSuccess, boolean outputMatch,
                         String compilationLog, String executionLog, String errorLog) {
        this.projectId = projectId;
        this.studentId = studentId;
        this.compilationSuccess = compilationSuccess;
        this.executionSuccess = executionSuccess;
        this.outputMatch = outputMatch;
        this.compilationLog = compilationLog;
        this.executionLog = executionLog;
        this.errorLog = errorLog;
    }

    public StudentResult(int id, int projectId, String studentId, boolean compilationSuccess,
                         boolean executionSuccess, boolean outputMatch,
                         String compilationLog, String executionLog, String errorLog) {
        this.id = id;
        this.projectId = projectId;
        this.studentId = studentId;
        this.compilationSuccess = compilationSuccess;
        this.executionSuccess = executionSuccess;
        this.outputMatch = outputMatch;
        this.compilationLog = compilationLog;
        this.executionLog = executionLog;
        this.errorLog = errorLog;
    }

    // GETTERS
    public int getId() { return id; }
    public int getProjectId() { return projectId; }
    public String getStudentId() { return studentId; }
    public boolean isCompilationSuccess() { return compilationSuccess; }
    public boolean isExecutionSuccess() { return executionSuccess; }
    public boolean isOutputMatch() { return outputMatch; }
    public String getCompilationLog() { return compilationLog; }
    public String getExecutionLog() { return executionLog; }
    public String getErrorLog() { return errorLog; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setCompilationSuccess(boolean compilationSuccess) { this.compilationSuccess = compilationSuccess; }
    public void setExecutionSuccess(boolean executionSuccess) { this.executionSuccess = executionSuccess; }
    public void setOutputMatch(boolean outputMatch) { this.outputMatch = outputMatch; }
    public void setCompilationLog(String compilationLog) { this.compilationLog = compilationLog; }
    public void setExecutionLog(String executionLog) { this.executionLog = executionLog; }
    public void setErrorLog(String errorLog) { this.errorLog = errorLog; }
}