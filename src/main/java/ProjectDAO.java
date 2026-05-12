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
        String sql = "INSERT INTO projects (name, description, configuration_id, zip_directory_path, expected_output_path, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setInt(3, project.getConfigId());
            statement.setString(4, project.getZipDirectoryPath());
            statement.setString(5, project.getExpectedOutputPath());
            statement.setString(6, LocalDateTime.now().toString());
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
        String sql = "UPDATE projects SET name=?, description=?, configuration_id=?, zip_directory_path=?, expected_output_path=? WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setInt(3, project.getConfigId());
            statement.setString(4, project.getZipDirectoryPath());
            statement.setString(5, project.getExpectedOutputPath());
            statement.setInt(6, project.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteProject(int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Project mapRow(ResultSet result) throws SQLException {
        return new Project(
                result.getInt("id"),
                result.getString("name"),
                result.getString("description"),
                result.getInt("configuration_id"),
                result.getString("zip_directory_path"),
                result.getString("expected_output_path"),
                result.getString("created_at") != null ? LocalDateTime.parse(result.getString("created_at")) : null
        );
    }
}
