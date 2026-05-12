import java.time.LocalDateTime;

public class Project {

    private int id;
    private String name;
    private String description;
    private int configurationId;
    private String zipDirectoryPath;
    private String expectedOutputPath;
    private LocalDateTime createdAt;


    public Project(int id, String name, String description, int configurationId, String zipDirectoryPath, String expectedOutputPath,  LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.configurationId = configurationId;
        this.zipDirectoryPath = zipDirectoryPath;
        this.expectedOutputPath = expectedOutputPath;
        this.createdAt = createdAt;
    }

    // GETTERS
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getConfigId() {
        return configurationId;
    }
    public String getZipDirectoryPath() {
        return zipDirectoryPath;
    }
    public String getExpectedOutputPath() {
        return expectedOutputPath;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // SETTERS
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setConfigId(int configurationId) {
        this.configurationId = configurationId;
    }
    public void setZipDirectoryPath(String zipDirectoryPath) {
        this.zipDirectoryPath = zipDirectoryPath;
    }
    public void setExpectedOutputPath(String expectedOutputPath) {
        this.expectedOutputPath = expectedOutputPath;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
