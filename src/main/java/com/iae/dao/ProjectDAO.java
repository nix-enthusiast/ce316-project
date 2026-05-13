package com.iae.dao;

import com.iae.model.Project;
import com.iae.service.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private final Connection connection;

    public ProjectDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public Project createProject(Project project) throws SQLException {
        String sql = "INSERT INTO projects (name, description, configuration_name, zip_directory, binary_args, expected_output_path, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setString(3, project.getConfigurationName());
            statement.setString(4, project.getZipDirectory());
            statement.setString(5, project.getBinaryArgs());
            statement.setString(6, project.getExpectedOutputPath());
            statement.setString(7, LocalDateTime.now().toString());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                project.setId(keys.getInt(1));
            }
        }
        return project;
    }

    public Project getProjectById(int id) throws SQLException {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return mapRow(result);
            }
        }
        return null;
    }

    public List<Project> getAllProjects() throws SQLException {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                list.add(mapRow(result));
            }
        }
        return list;
    }

    public boolean updateProject(Project project) throws SQLException {
        String sql = "UPDATE projects SET name=?, description=?, configuration_name=?, zip_directory=?, binary_args=?, expected_output_path=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setString(3, project.getConfigurationName());
            statement.setString(4, project.getZipDirectory());
            statement.setString(5, project.getBinaryArgs());
            statement.setString(6, project.getExpectedOutputPath());
            statement.setInt(7, project.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Project mapRow(ResultSet result) throws SQLException {
        return new Project(
                result.getInt("id"),
                result.getString("name"),
                result.getString("description"),
                result.getString("configuration_name"),
                result.getString("zip_directory"),
                result.getString("binary_args"),
                result.getString("expected_output_path"),
                result.getString("created_at") != null ? LocalDateTime.parse(result.getString("created_at")) : null
        );
    }
}