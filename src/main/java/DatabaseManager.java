import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        String dbPath = System.getProperty("user.home") + "/IAE/iae_data.db";
        new java.io.File(System.getProperty("user.home") + "/IAE").mkdirs();
        String url = "jdbc:sqlite:" + dbPath;
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            initSchema();
        } catch (SQLException e) {
            System.out.println("Connection to SQLite failed.");
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    private void initSchema() throws SQLException {
        String createProjects = """
            CREATE TABLE IF NOT EXISTS projects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                configuration_id INTEGER NOT NULL,
                zip_directory_path TEXT,
                expected_output_path TEXT,
                created_at TEXT
            );
        """;
        String createResults = """
            CREATE TABLE IF NOT EXISTS student_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_id INTEGER NOT NULL,
                student_id TEXT NOT NULL,
                compilation_success INTEGER,
                run_success INTEGER,
                output_match INTEGER,
                error_message TEXT,
                run_at TEXT,
                FOREIGN KEY(project_id) REFERENCES projects(id)
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createProjects);
            stmt.execute(createResults);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try{
            if(connection != null){
                System.out.println("Closing SQLite connection.");
                connection.close();
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
