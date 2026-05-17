# CE316 — Integrated Assignment Environment (IAE)

A lightweight desktop application for managing and automatically evaluating programming assignments. Lecturers can create projects, configure language environments, run student submissions through a pipeline, and view per-student results — all from a single GUI.

---

## Team

| Name | Role |
|------|------|
| Görkem | Configuration Manager |
| Jahangir | Project Manager |
| Hüseyin | Pipeline Executor |
| Burak | Output Comparator |
| Murat | UI Shell & Integration |

---

## Requirements

- **Java 17 or higher** must be installed on your system
- **Maven 3.8 or higher**
- JavaFX is bundled via Maven — no separate installation needed
- Any compiler/interpreter you want to test with (e.g. `gcc`, `python3`) must be installed and accessible from the command line

To verify:
```bash
java -version
mvn -version
```

---

## How to Run

### 1. Clone the repository
```bash
git clone https://github.com/nix-enthusiast/ce316-project.git
cd ce316-project
```

### 2. Build and launch
```bash
mvn clean javafx:run
```

The application window opens at 800×600 titled **"CE316 Project - Main Dashboard"**.

On first run, the following are created automatically:
- SQLite database: `<user.home>/IAE/iae_data.db`
- Configurations folder: `<user.home>/IAE/configs/`

---

## Application Screens

The app has a menu bar at the top and a content area below. Navigation happens through the menu.

### Dashboard (Project List)
The default screen when the app opens. Shows all saved projects in a list. Each entry displays the project name and its configuration in brackets.

**Buttons:**
- **New Project** — opens a dialog to create a project
- **Open Project** — opens the selected project in the workspace
- **Delete Project** — deletes the selected project from the database

### Configuration Manager (`Tools > Configuration Manager`)
Manage language configurations. Configurations are stored as `.json` files.

**Fields:**
- **Name** — identifier for the configuration (e.g. `C Language`)
- **Compiler Path** — path or command for the compiler (e.g. `gcc`). Leave empty for interpreted languages.
- **Compiler Args** — arguments passed to the compiler (e.g. `main.c -o main`)
- **Binary Path** — the executable to run after compilation (e.g. `main`), or the interpreter for interpreted languages (e.g. `python3`)
- **Binary Args** — default runtime arguments (e.g. a script filename for Python)

**Buttons:** New, Save, Delete, Import, Export

### Project Workspace
Opened when you create or open a project. Shows the project name and configuration at the top.

**Fields:**
- **ZIP Directory** — folder containing all student `.zip` submission files (browse button available)
- **Binary Args** — command line arguments passed to each student's program at runtime
- **Expected Output** — path to a text file containing the correct program output (browse button available)

**Run Pipeline button** — starts the automated grading process. A progress bar and status label show real-time progress. The button is disabled while the pipeline is running to prevent double execution.

### Results Grid
Displayed automatically after the pipeline finishes. Shows a table with one row per student.

**Columns:** Student ID | Compiled | Executed | Output Match

**Buttons:**
- **View Details** — opens a popup showing the full compilation log, execution log, and error log for the selected student
- **Back to Workspace** — returns to the project workspace screen

### Help (`Help > Help Manual`)
Displays the application manual inside the app.

---

## Step-by-Step Usage

### Step 1 — Create a Configuration

1. Go to **Tools > Configuration Manager**
2. Click **New** and fill in the fields
3. Click **Save**

**Example — C Language:**

| Field | Value |
|-------|-------|
| Name | C Language |
| Compiler Path | `gcc` |
| Compiler Args | `main.c -o main` |
| Binary Path | `main` |
| Binary Args | *(leave empty, set per-project)* |

**Example — Python:**

| Field | Value |
|-------|-------|
| Name | Python |
| Compiler Path | *(leave empty)* |
| Compiler Args | *(leave empty)* |
| Binary Path | `python3` |
| Binary Args | `main.py` |

### Step 2 — Create a Project

1. On the Dashboard, click **New Project**
2. Fill in:
    - **Name** — e.g. `Sorting Assignment`
    - **Description** — optional
    - **Configuration** — select from the dropdown (must exist first)
3. Click **Create** — the project is saved and the workspace opens immediately

> If no configurations exist yet, you will be prompted to create one first.

### Step 3 — Set Up and Run the Pipeline

In the Project Workspace:
1. Click **Browse** next to **ZIP Directory** and select the folder containing student ZIPs
2. Enter **Binary Args** — the runtime arguments passed to every student's program (e.g. `apple banana cherry`)
3. Click **Browse** next to **Expected Output** and select your expected output `.txt` file
4. Click **Run Pipeline**

The pipeline will:
1. Find all `.zip` files in the selected directory
2. Extract each one into a subfolder named after the student ID
3. Compile the source code using the project's configuration
4. Run the compiled binary (or interpreter) with the provided arguments
5. Compare stdout against the expected output file
6. Save all results to the database
7. Automatically navigate to the Results Grid when complete

If a student's ZIP is corrupted, compilation fails, or execution fails, the pipeline records the error and **continues to the next student** without stopping.

### Step 4 — View Results

The Results Grid shows pass/fail for each pipeline step per student. Click **View Details** on any row to see the full compilation log, execution log, and error log for that student.

Results are persisted — you can close the app, reopen the project later from the Dashboard, and all results will still be there.

### Step 5 — Import / Export Configurations

- **Export:** Select a config in Configuration Manager → click **Export** → choose save location → saves as `<name>.json`
- **Import:** Click **Import** → select a `.json` file → it appears in the list immediately

This lets you share configurations between computers without recreating them.

---

## Student ZIP Format

ZIP filenames must be the student ID (e.g. `20200001.zip`). The student ID shown in the results table is taken directly from the ZIP filename (without the `.zip` extension).

The ZIP should contain the source file at the root or inside a single subdirectory — the extractor handles both cases automatically.

```
submissions/
├── 20200001.zip   →  extracted/20200001/main.c
├── 20200002.zip   →  extracted/20200002/main.c
└── 20200003.zip   →  extracted/20200003/main.c
```

Extracted files are placed in an `extracted/` subfolder inside the ZIP directory and are not deleted after the run.

---

## Project Structure

```
src/main/java/com/iae/
├── controller/
│   ├── App.java                        # JavaFX entry point
│   ├── MainController.java             # Main window shell + navigation
│   ├── ConfigurationController.java    # Configuration Manager screen
│   ├── ProjectListController.java      # Dashboard / project list screen
│   ├── ProjectWorkspaceController.java # Project setup + pipeline orchestrator
│   └── ResultsGridController.java      # Results table screen
├── model/
│   ├── Configuration.java              # Language config POJO (JSON-backed)
│   ├── Project.java                    # Project POJO (SQLite-backed)
│   └── StudentResult.java              # Per-student result POJO
├── dao/
│   ├── ProjectDAO.java                 # Project CRUD (SQLite)
│   └── StudentResultDAO.java           # Result save/load (SQLite)
├── service/
│   ├── DatabaseManager.java            # SQLite singleton + schema creation
│   ├── ConfigurationManager.java       # Config file read/write/import/export
│   └── ProjectService.java             # Service layer used by controllers
└── ExecutionPipeline/
    ├── ZipExtractor.java               # Extracts all student ZIPs from a directory
    ├── ExecutionManager.java           # Compiles and runs programs via ProcessBuilder
    └── EvaluationEngine.java           # Compares actual vs expected output

src/main/resources/com/iae/controller/
├── main-view.fxml                      # Main window layout with menu bar
├── configuration-view.fxml             # Configuration Manager screen
├── project-list-view.fxml              # Dashboard screen
├── project-workspace-view.fxml         # Project workspace + Run button
├── results-grid-view.fxml              # Results table screen
└── help-view.fxml                      # Help manual screen
```

---

## Dependencies

Managed by Maven and downloaded automatically on first build.

| Dependency | Version | Purpose |
|------------|---------|---------|
| JavaFX | 21 | GUI framework |
| SQLite JDBC (`org.xerial`) | 3.45.3.0 | Local file-based database |
| Gson (`com.google.gson`) | 2.10.1 | JSON config file read/write |

---

## Data Storage

| Data | Location |
|------|----------|
| SQLite database (projects + results) | `<user.home>/IAE/iae_data.db` |
| Configuration JSON files | `<user.home>/IAE/configs/` |
| Extracted student files | `<zip_directory>/extracted/` |

No server or external database software required. Everything is file-based and self-contained.

---

## Pipeline Execution Details

- Students are processed **sequentially**, one at a time
- Compilation and execution each have a **20-second timeout** — if a student's program hangs, it is killed and marked as failed
- The pipeline runs on a **background thread** so the UI remains responsive during execution
- If compilation fails for a student, execution and output comparison are skipped for that student
- Output comparison is **line-by-line**, trimming whitespace and normalizing line endings (`\r\n` vs `\n`)
- Re-running a project **clears previous results** before saving new ones

---

## Milestone Status

| Milestone | Deadline | Status |
|-----------|----------|--------|
| M1 — Design Document | May 3, 2026 | ✅ Submitted |
| M2 — Working Prototype | May 17, 2026 | ✅ Complete |
| M3 — Project Delivery | May 31, 2026 | ⏳ Pending |

### M2 Requirements Coverage

| Req | Description | Status |
|-----|-------------|--------|
| Req 3 | Create a project using an existing or new configuration | ✅ |
| Req 4 | Create, edit, and remove configurations | ✅ |
| Req 5 | Import and export configurations | ✅ |
| Req 6 | Process all ZIP files in a directory automatically | ✅ |
| Req 7 | Compile or interpret source code using project configuration | ✅ |
| Req 8 | Compare student output against expected output | ✅ |
| Req 9 | Display results per student | ✅ |
| Req 10 | Open and save projects at any time | ✅ |
| Req 2 | Help manual accessible from menu | ✅ |
| Req 1 | Windows installer with desktop shortcut | ⏳ M3 |