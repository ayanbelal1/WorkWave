package com.timetracker.controller;

import com.timetracker.dao.*;
import com.timetracker.model.*;
import com.timetracker.service.AuthenticationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ProjectManagerDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Task Assignment
    @FXML private TableView<TaskView> tasksTable;
    @FXML private TableColumn<TaskView, Integer> taskIdColumn;
    @FXML private TableColumn<TaskView, String> taskTitleColumn;
    @FXML private TableColumn<TaskView, String> projectColumn;
    @FXML private TableColumn<TaskView, String> assigneeColumn;
    @FXML private TableColumn<TaskView, String> statusColumn;
    @FXML private TableColumn<TaskView, LocalDate> dueDateColumn;
    @FXML private TableColumn<TaskView, Integer> estimatedHoursColumn;
    @FXML private TableColumn<TaskView, Double> actualHoursColumn;
    @FXML private Button assignTaskButton;
    
    // Time Tracking Monitor
    @FXML private PieChart projectTimeChart;
    @FXML private BarChart<String, Number> memberTimeChart;
    
    // Progress Review
    @FXML private TableView<ProjectProgress> progressTable;
    @FXML private TableColumn<ProjectProgress, String> progressProjectColumn;
    @FXML private TableColumn<ProjectProgress, Integer> totalTasksColumn;
    @FXML private TableColumn<ProjectProgress, Integer> completedTasksColumn;
    @FXML private TableColumn<ProjectProgress, Integer> inProgressTasksColumn;
    @FXML private TableColumn<ProjectProgress, Integer> pendingTasksColumn;
    @FXML private TableColumn<ProjectProgress, Double> progressPercentColumn;
    @FXML private TableColumn<ProjectProgress, ProgressBar> progressBarColumn;
    @FXML private Button refreshProgressButton;
    @FXML private Button exportReportButton;

    private AuthenticationService authService;
    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private TimeLogDAO timeLogDAO;

    public void initialize() {
        authService = new AuthenticationService();
        taskDAO = new TaskDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
        timeLogDAO = new TimeLogDAO();
        
        setupWelcomeMessage();
        setupTaskTable();
        setupProgressTable();
        loadData();
    }

    private void setupWelcomeMessage() {
        User currentUser = authService.getCurrentUser();
        welcomeLabel.setText("Welcome, " + currentUser.getFullName());
    }

    private void setupTaskTable() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        assigneeColumn.setCellValueFactory(new PropertyValueFactory<>("assigneeName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        estimatedHoursColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedHours"));
        actualHoursColumn.setCellValueFactory(new PropertyValueFactory<>("actualHours"));
    }

    private void setupProgressTable() {
        progressProjectColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        totalTasksColumn.setCellValueFactory(new PropertyValueFactory<>("totalTasks"));
        completedTasksColumn.setCellValueFactory(new PropertyValueFactory<>("completedTasks"));
        inProgressTasksColumn.setCellValueFactory(new PropertyValueFactory<>("inProgressTasks"));
        pendingTasksColumn.setCellValueFactory(new PropertyValueFactory<>("pendingTasks"));
        progressPercentColumn.setCellValueFactory(new PropertyValueFactory<>("progressPercent"));
        
        progressBarColumn.setCellValueFactory(new PropertyValueFactory<>("progressBar"));
    }

    private void loadData() {
        loadTasks();
        loadCharts();
        loadProgressData();
    }

    private void loadTasks() {
        List<Task> tasks = taskDAO.getAllTasks();
        ObservableList<TaskView> taskViews = FXCollections.observableArrayList();
        
        for (Task task : tasks) {
            Project project = projectDAO.getProjectById(task.getProjectId());
            String projectName = project != null ? project.getTitle() : "Unknown";
            
            // Get assignee name (simplified)
            String assigneeName = "User " + task.getAssignedUserId();
            
            // Get actual hours
            double actualHours = timeLogDAO.getTotalHoursByTaskId(task.getId());
            
            TaskView taskView = new TaskView(
                task.getId(),
                task.getTitle(),
                projectName,
                assigneeName,
                task.getStatus().getDisplayName(),
                task.getDueDate(),
                task.getEstimatedHours(),
                actualHours
            );
            taskViews.add(taskView);
        }
        
        tasksTable.setItems(taskViews);
    }

    private void loadCharts() {
        // Project Time Chart
        ObservableList<PieChart.Data> projectData = FXCollections.observableArrayList();
        projectData.add(new PieChart.Data("E-Commerce Platform", 120));
        projectData.add(new PieChart.Data("Mobile App Development", 85));
        projectData.add(new PieChart.Data("Data Analytics Dashboard", 65));
        projectTimeChart.setData(projectData);
        
        // Member Time Chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Hours Worked");
        series.getData().add(new XYChart.Data<>("Alice Developer", 45));
        series.getData().add(new XYChart.Data<>("Bob Developer", 38));
        series.getData().add(new XYChart.Data<>("Charlie Designer", 32));
        memberTimeChart.getData().add(series);
    }

    private void loadProgressData() {
        ObservableList<ProjectProgress> progressData = FXCollections.observableArrayList();
        
        // Sample progress data
        progressData.add(new ProjectProgress("E-Commerce Platform", 8, 3, 3, 2, 37.5));
        progressData.add(new ProjectProgress("Mobile App Development", 6, 2, 2, 2, 33.3));
        progressData.add(new ProjectProgress("Data Analytics Dashboard", 4, 1, 1, 2, 25.0));
        
        progressTable.setItems(progressData);
    }

    @FXML
    private void handleAssignTask() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Assign New Task");
        dialog.setHeaderText("Create and assign a new task:");

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField title = new TextField();
        TextArea description = new TextArea();
        ComboBox<Project> projectCombo = new ComboBox<>();
        ComboBox<User> assigneeCombo = new ComboBox<>();
        DatePicker dueDate = new DatePicker(LocalDate.now().plusWeeks(2));
        TextField estimatedHours = new TextField();

        // Load projects and users
        projectCombo.setItems(FXCollections.observableArrayList(projectDAO.getAllProjects()));
        List<User> teamMembers = userDAO.getAllUsers().stream()
            .filter(u -> u.getRole() == UserRole.TEAM_MEMBER)
            .toList();
        assigneeCombo.setItems(FXCollections.observableArrayList(teamMembers));

        description.setPrefRowCount(3);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("Project:"), 0, 2);
        grid.add(projectCombo, 1, 2);
        grid.add(new Label("Assignee:"), 0, 3);
        grid.add(assigneeCombo, 1, 3);
        grid.add(new Label("Due Date:"), 0, 4);
        grid.add(dueDate, 1, 4);
        grid.add(new Label("Estimated Hours:"), 0, 5);
        grid.add(estimatedHours, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                try {
                    return new Task(
                        title.getText(),
                        description.getText(),
                        projectCombo.getValue().getId(),
                        assigneeCombo.getValue().getId(),
                        dueDate.getValue(),
                        Integer.parseInt(estimatedHours.getText())
                    );
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            if (taskDAO.createTask(task)) {
                loadTasks();
                showAlert("Success", "Task assigned successfully!");
            } else {
                showAlert("Error", "Failed to assign task!");
            }
        });
    }

    @FXML
    private void handleRefreshProgress() {
        loadProgressData();
        showAlert("Refresh", "Progress data refreshed!");
    }

    @FXML
    private void handleExportReport() {
        showAlert("Export", "Report export functionality would be implemented here!");
    }

    @FXML
    private void handleLogout() {
        try {
            authService.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Time Tracking Tool");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper classes for table views
    public static class TaskView {
        private int id;
        private String title;
        private String projectName;
        private String assigneeName;
        private String status;
        private LocalDate dueDate;
        private int estimatedHours;
        private double actualHours;

        public TaskView(int id, String title, String projectName, String assigneeName, 
                       String status, LocalDate dueDate, int estimatedHours, double actualHours) {
            this.id = id;
            this.title = title;
            this.projectName = projectName;
            this.assigneeName = assigneeName;
            this.status = status;
            this.dueDate = dueDate;
            this.estimatedHours = estimatedHours;
            this.actualHours = actualHours;
        }

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getProjectName() { return projectName; }
        public String getAssigneeName() { return assigneeName; }
        public String getStatus() { return status; }
        public LocalDate getDueDate() { return dueDate; }
        public int getEstimatedHours() { return estimatedHours; }
        public double getActualHours() { return actualHours; }
    }

    public static class ProjectProgress {
        private String projectName;
        private int totalTasks;
        private int completedTasks;
        private int inProgressTasks;
        private int pendingTasks;
        private double progressPercent;
        private ProgressBar progressBar;

        public ProjectProgress(String projectName, int totalTasks, int completedTasks, 
                             int inProgressTasks, int pendingTasks, double progressPercent) {
            this.projectName = projectName;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.inProgressTasks = inProgressTasks;
            this.pendingTasks = pendingTasks;
            this.progressPercent = progressPercent;
            this.progressBar = new ProgressBar(progressPercent / 100.0);
        }

        // Getters
        public String getProjectName() { return projectName; }
        public int getTotalTasks() { return totalTasks; }
        public int getCompletedTasks() { return completedTasks; }
        public int getInProgressTasks() { return inProgressTasks; }
        public int getPendingTasks() { return pendingTasks; }
        public double getProgressPercent() { return progressPercent; }
        public ProgressBar getProgressBar() { return progressBar; }
    }
}