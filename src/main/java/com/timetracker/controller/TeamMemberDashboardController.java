package com.timetracker.controller;

import com.timetracker.dao.*;
import com.timetracker.model.*;
import com.timetracker.service.AuthenticationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeamMemberDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // Log Time Tab
    @FXML private ComboBox<Task> taskComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField hoursField;
    @FXML private TextArea descriptionArea;
    @FXML private Button logTimeButton;
    @FXML private Button quickLog1Button;
    @FXML private Button quickLog4Button;
    @FXML private Button quickLog8Button;
    
    // Time Logs Tab
    @FXML private TableView<TimeLogView> timeLogsTable;
    @FXML private TableColumn<TimeLogView, LocalDate> logDateColumn;
    @FXML private TableColumn<TimeLogView, String> logTaskColumn;
    @FXML private TableColumn<TimeLogView, String> logProjectColumn;
    @FXML private TableColumn<TimeLogView, Double> logHoursColumn;
    @FXML private TableColumn<TimeLogView, String> logDescriptionColumn;
    @FXML private TableColumn<TimeLogView, String> logCreatedColumn;
    @FXML private DatePicker filterStartDate;
    @FXML private DatePicker filterEndDate;
    @FXML private Button filterButton;
    @FXML private Label totalHoursLabel;
    @FXML private Button refreshLogsButton;
    
    // My Tasks Tab
    @FXML private TableView<MyTaskView> myTasksTable;
    @FXML private TableColumn<MyTaskView, Integer> myTaskIdColumn;
    @FXML private TableColumn<MyTaskView, String> myTaskTitleColumn;
    @FXML private TableColumn<MyTaskView, String> myTaskProjectColumn;
    @FXML private TableColumn<MyTaskView, String> myTaskStatusColumn;
    @FXML private TableColumn<MyTaskView, LocalDate> myTaskDueDateColumn;
    @FXML private TableColumn<MyTaskView, Integer> myTaskEstHoursColumn;
    @FXML private TableColumn<MyTaskView, Double> myTaskActualHoursColumn;
    @FXML private TableColumn<MyTaskView, Void> myTaskActionsColumn;
    @FXML private Button refreshTasksButton;

    private AuthenticationService authService;
    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private TimeLogDAO timeLogDAO;
    private User currentUser;

    public void initialize() {
        authService = new AuthenticationService();
        taskDAO = new TaskDAO();
        projectDAO = new ProjectDAO();
        timeLogDAO = new TimeLogDAO();
        currentUser = authService.getCurrentUser();
        
        setupWelcomeMessage();
        setupTables();
        setupDatePickers();
        loadData();
    }

    private void setupWelcomeMessage() {
        welcomeLabel.setText("Welcome, " + currentUser.getFullName());
    }

    private void setupTables() {
        // Time Logs Table
        logDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        logTaskColumn.setCellValueFactory(new PropertyValueFactory<>("taskTitle"));
        logProjectColumn.setCellValueFactory(new PropertyValueFactory<>("projectTitle"));
        logHoursColumn.setCellValueFactory(new PropertyValueFactory<>("hours"));
        logDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        logCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // My Tasks Table
        myTaskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        myTaskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        myTaskProjectColumn.setCellValueFactory(new PropertyValueFactory<>("projectTitle"));
        myTaskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        myTaskDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        myTaskEstHoursColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedHours"));
        myTaskActualHoursColumn.setCellValueFactory(new PropertyValueFactory<>("actualHours"));
        
        // Add action buttons to tasks table
        myTaskActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateStatusButton = new Button("Update Status");
            
            {
                updateStatusButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
                updateStatusButton.setOnAction(event -> {
                    MyTaskView task = getTableView().getItems().get(getIndex());
                    handleUpdateTaskStatus(task);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateStatusButton);
                }
            }
        });
    }

    private void setupDatePickers() {
        datePicker.setValue(LocalDate.now());
        filterStartDate.setValue(LocalDate.now().minusWeeks(2));
        filterEndDate.setValue(LocalDate.now());
    }

    private void loadData() {
        loadMyTasks();
        loadMyTimeLogs();
        updateTotalHours();
    }

    private void loadMyTasks() {
        List<Task> tasks = taskDAO.getTasksByUserId(currentUser.getId());
        
        // Load for combo box
        taskComboBox.setItems(FXCollections.observableArrayList(tasks));
        if (!tasks.isEmpty()) {
            taskComboBox.getSelectionModel().selectFirst();
        }
        
        // Load for my tasks table
        ObservableList<MyTaskView> taskViews = FXCollections.observableArrayList();
        for (Task task : tasks) {
            Project project = projectDAO.getProjectById(task.getProjectId());
            String projectTitle = project != null ? project.getTitle() : "Unknown";
            double actualHours = timeLogDAO.getTotalHoursByTaskId(task.getId());
            
            MyTaskView taskView = new MyTaskView(
                task.getId(),
                task.getTitle(),
                projectTitle,
                task.getStatus().getDisplayName(),
                task.getDueDate(),
                task.getEstimatedHours(),
                actualHours
            );
            taskViews.add(taskView);
        }
        
        myTasksTable.setItems(taskViews);
    }

    private void loadMyTimeLogs() {
        List<TimeLog> timeLogs = timeLogDAO.getTimeLogsByUserId(currentUser.getId());
        ObservableList<TimeLogView> logViews = FXCollections.observableArrayList();
        
        for (TimeLog log : timeLogs) {
            // Get task and project info
            String taskTitle = "Task " + log.getTaskId(); // Simplified
            String projectTitle = "Project"; // Simplified
            
            String createdAt = log.getCreatedAt() != null ? 
                log.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : "";
            
            TimeLogView logView = new TimeLogView(
                log.getDate(),
                taskTitle,
                projectTitle,
                log.getHoursSpent(),
                log.getDescription(),
                createdAt
            );
            logViews.add(logView);
        }
        
        timeLogsTable.setItems(logViews);
    }

    private void updateTotalHours() {
        double total = timeLogsTable.getItems().stream()
            .mapToDouble(TimeLogView::getHours)
            .sum();
        totalHoursLabel.setText(String.format("Total Hours: %.1f", total));
    }

    @FXML
    private void handleLogTime() {
        Task selectedTask = taskComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String hoursText = hoursField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (selectedTask == null) {
            showAlert("Error", "Please select a task");
            return;
        }

        if (selectedDate == null) {
            showAlert("Error", "Please select a date");
            return;
        }

        if (hoursText.isEmpty()) {
            showAlert("Error", "Please enter hours spent");
            return;
        }

        try {
            double hours = Double.parseDouble(hoursText);
            if (hours <= 0 || hours > 24) {
                showAlert("Error", "Hours must be between 0 and 24");
                return;
            }

            TimeLog timeLog = new TimeLog(
                selectedTask.getId(),
                currentUser.getId(),
                selectedDate,
                hours,
                description
            );

            if (timeLogDAO.createTimeLog(timeLog)) {
                // Clear form
                hoursField.clear();
                descriptionArea.clear();
                
                // Refresh data
                loadMyTimeLogs();
                loadMyTasks(); // Refresh to update actual hours
                updateTotalHours();
                
                showAlert("Success", "Time logged successfully!");
            } else {
                showAlert("Error", "Failed to log time!");
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for hours");
        }
    }

    @FXML
    private void handleQuickLog1() {
        hoursField.setText("1.0");
    }

    @FXML
    private void handleQuickLog4() {
        hoursField.setText("4.0");
    }

    @FXML
    private void handleQuickLog8() {
        hoursField.setText("8.0");
    }

    @FXML
    private void handleFilter() {
        LocalDate startDate = filterStartDate.getValue();
        LocalDate endDate = filterEndDate.getValue();
        
        if (startDate != null && endDate != null) {
            List<TimeLog> filteredLogs = timeLogDAO.getTimeLogsByDateRange(startDate, endDate)
                .stream()
                .filter(log -> log.getUserId() == currentUser.getId())
                .toList();
            
            ObservableList<TimeLogView> logViews = FXCollections.observableArrayList();
            for (TimeLog log : filteredLogs) {
                String taskTitle = "Task " + log.getTaskId();
                String projectTitle = "Project";
                String createdAt = log.getCreatedAt() != null ? 
                    log.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : "";
                
                TimeLogView logView = new TimeLogView(
                    log.getDate(),
                    taskTitle,
                    projectTitle,
                    log.getHoursSpent(),
                    log.getDescription(),
                    createdAt
                );
                logViews.add(logView);
            }
            
            timeLogsTable.setItems(logViews);
            updateTotalHours();
        }
    }

    @FXML
    private void handleRefreshLogs() {
        loadMyTimeLogs();
        updateTotalHours();
    }

    @FXML
    private void handleRefreshTasks() {
        loadMyTasks();
    }

    private void handleUpdateTaskStatus(MyTaskView taskView) {
        ChoiceDialog<Task.TaskStatus> dialog = new ChoiceDialog<>(
            Task.TaskStatus.TODO, Task.TaskStatus.values());
        dialog.setTitle("Update Task Status");
        dialog.setHeaderText("Update status for: " + taskView.getTitle());
        dialog.setContentText("Select new status:");

        dialog.showAndWait().ifPresent(newStatus -> {
            if (taskDAO.updateTaskStatus(taskView.getId(), newStatus)) {
                loadMyTasks();
                showAlert("Success", "Task status updated successfully!");
            } else {
                showAlert("Error", "Failed to update task status!");
            }
        });
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
    public static class TimeLogView {
        private LocalDate date;
        private String taskTitle;
        private String projectTitle;
        private double hours;
        private String description;
        private String createdAt;

        public TimeLogView(LocalDate date, String taskTitle, String projectTitle, 
                          double hours, String description, String createdAt) {
            this.date = date;
            this.taskTitle = taskTitle;
            this.projectTitle = projectTitle;
            this.hours = hours;
            this.description = description;
            this.createdAt = createdAt;
        }

        // Getters
        public LocalDate getDate() { return date; }
        public String getTaskTitle() { return taskTitle; }
        public String getProjectTitle() { return projectTitle; }
        public double getHours() { return hours; }
        public String getDescription() { return description; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class MyTaskView {
        private int id;
        private String title;
        private String projectTitle;
        private String status;
        private LocalDate dueDate;
        private int estimatedHours;
        private double actualHours;

        public MyTaskView(int id, String title, String projectTitle, String status, 
                         LocalDate dueDate, int estimatedHours, double actualHours) {
            this.id = id;
            this.title = title;
            this.projectTitle = projectTitle;
            this.status = status;
            this.dueDate = dueDate;
            this.estimatedHours = estimatedHours;
            this.actualHours = actualHours;
        }

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getProjectTitle() { return projectTitle; }
        public String getStatus() { return status; }
        public LocalDate getDueDate() { return dueDate; }
        public int getEstimatedHours() { return estimatedHours; }
        public double getActualHours() { return actualHours; }
    }
}