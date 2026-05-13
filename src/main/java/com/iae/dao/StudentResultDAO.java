package com.iae.dao;

import com.iae.model.StudentResult;
import com.iae.service.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentResultDAO {
    private final Connection connection;

    public StudentResultDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void saveResult(StudentResult studentResult) throws SQLException {
        String sql = "INSERT INTO results (project_id, student_id, compilation_success, execution_success, output_match, compilation_log, execution_log, error_log) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, studentResult.getProjectId());
            statement.setString(2, studentResult.getStudentId());
            statement.setInt(3, studentResult.isCompilationSuccess() ? 1 : 0);
            statement.setInt(4, studentResult.isExecutionSuccess() ? 1 : 0);
            statement.setInt(5, studentResult.isOutputMatch() ? 1 : 0);
            statement.setString(6, studentResult.getCompilationLog());
            statement.setString(7, studentResult.getExecutionLog());
            statement.setString(8, studentResult.getErrorLog());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                studentResult.setId(keys.getInt(1));
            }
        }
    }

    public List<StudentResult> getResultsByProjectId(int projectId) throws SQLException {
        List<StudentResult> list = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                list.add(new StudentResult(
                        result.getInt("id"),
                        result.getInt("project_id"),
                        result.getString("student_id"),
                        result.getInt("compilation_success") == 1,
                        result.getInt("execution_success") == 1,
                        result.getInt("output_match") == 1,
                        result.getString("compilation_log"),
                        result.getString("execution_log"),
                        result.getString("error_log")
                ));
            }
        }
        return list;
    }

    public void deleteResultsByProjectId(int projectId) throws SQLException {
        String sql = "DELETE FROM results WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, projectId);
            statement.executeUpdate();
        }
    }
}