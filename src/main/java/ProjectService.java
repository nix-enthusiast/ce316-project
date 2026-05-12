import java.sql.SQLException;
import java.util.List;

public class ProjectService {
    private final ProjectDAO projectDAO;
    private final StudentResultDAO studentResultDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
        this.studentResultDAO = new StudentResultDAO();
    }

    public Project createProject(Project project) throws SQLException {
        return projectDAO.createProject(project);
    }

    public Project openProject(int id) throws SQLException {
        return projectDAO.getProjectById(id);
    }

    public List<Project> getAllProjects() throws SQLException {
        return projectDAO.getAllProjects();
    }

    public boolean saveProject(Project project) throws SQLException {
        return projectDAO.updateProject(project);
    }

    public boolean deleteProject(int id) throws SQLException {
        return projectDAO.deleteProject(id);
    }

    public void saveStudentResult(StudentResult result) throws SQLException {
        studentResultDAO.saveResult(result);
    }

    public List<StudentResult> getResultsForProject(int projectId) throws SQLException {
        return studentResultDAO.getResultsByProjectId(projectId);
    }

    public void clearResults(int projectId) throws SQLException {
        studentResultDAO.deleteResultsByProjectId(projectId);
    }

}
