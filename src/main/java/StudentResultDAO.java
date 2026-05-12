import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentResultDAO {
    private final Connection connection;

    public StudentResultDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void saveResult(StudentResult studentResult) throws SQLException {
        String sql = "INSERT INTO student_results (project_id, student_id, compilation_success, run_success, output_match, error_message, run_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql,  Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, studentResult.getProjectId());
            statement.setString(2, studentResult.getStudentId());
            statement.setInt(3, studentResult.isCompilationSuccess() ? 1 : 0);
            statement.setInt(4, studentResult.isRunSuccess()  ? 1 : 0);
            statement.setInt(5, studentResult.isOutputMatch() ? 1 : 0);
            statement.setString(6, studentResult.getErrorMessage());
            statement.setString(7, studentResult.getRunAt() != null ? studentResult.getRunAt().toString() : LocalDateTime.now().toString());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                studentResult.setId(keys.getInt(1));
            }
        }
    }

        public List<StudentResult> getResultsByProjectId(int projectId) throws SQLException {
            List<StudentResult> list = new ArrayList<>();
            String sql = "SELECT * FROM student_results WHERE project_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, projectId);
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    list.add(new StudentResult(
                            result.getInt("id"),
                            result.getInt("project_id"),
                            result.getString("student_id"),
                            result.getInt("compilation_success") == 1,
                            result.getInt("run_success") == 1,
                            result.getInt("output_match") == 1,
                            result.getString("error_message"),
                            result.getString("run_at") != null ? LocalDateTime.parse(result.getString("run_at")) : null
                    ));
                }
            }
            return list;
        }

        public void deleteResultsByProjectId(int projectId) throws SQLException {
            String sql = "DELETE FROM student_results WHERE project_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, projectId);
                statement.executeUpdate();
            }
        }
}
