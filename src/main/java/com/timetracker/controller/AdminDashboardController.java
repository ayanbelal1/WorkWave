package com.timetracker.controller;

import com.timetracker.dao.*;
import com.timetracker.model.*;
import com.timetracker.service.AuthenticationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    
    // User Management
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, UserRole> roleColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private Button addUserButton;
    
    // Project Management
    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, Integer> projectIdColumn;
    @FXML private TableColumn<Project, String> projectTitleColumn;
    @FXML private TableColumn<Project, String> projectDescriptionColumn;
    @FXML private TableColumn<Project, LocalDate> startDateColumn;
    @FXML private TableColumn<Project, LocalDate> endDateColumn;
    @FXML private TableColumn<Project, Project.ProjectStatus> statusColumn;
    @FXML private Button addProjectButton;
    
    // Integration
    @FXML private Label integrationStatusLabel;
    @FXML private Button testIntegrationButton;
    
    // Reports
    @FXML private ComboBox<Project> projectComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateReportButton;
    @FXML private PieChart timeDistributionChart;

    private AuthenticationService authService;
    private UserDAO userDAO;
    private ProjectDAO projectDAO;
    private TimeLogDAO timeLogDAO;

    public void initialize() {
        authService = new AuthenticationService();
        userDAO = new UserDAO();
        projectDAO = new ProjectDAO();
        timeLogDAO = new TimeLogDAO();
        
        setupWelcomeMessage();
        setupUserTable();
        setupProjectTable();
        setupReportsSection();
        loadData();
    }

    private void setupWelcomeMessage() {
        User currentUser = authService.getCurrentUser();
        welcomeLabel.setText("Welcome, " + currentUser.getFullName());
    }

    private void setupUserTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Add action buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit Role");
            
            {
                editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUserRole(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    private void setupProjectTable() {
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        projectDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupReportsSection() {
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
    }

    private void loadData() {
        loadUsers();
        loadProjects();
        loadProjectComboBox();
        generateDefaultReport();
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }

    private void loadProjects() {
        List<Project> projects = projectDAO.getAllProjects();
        projectsTable.setItems(FXCollections.observableArrayList(projects));
    }

    private void loadProjectComboBox() {
        List<Project> projects = projectDAO.getAllProjects();
        projectComboBox.setItems(FXCollections.observableArrayList(projects));
        if (!projects.isEmpty()) {
            projectComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleAddUser() {
        // Simple dialog for adding user
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField username = new TextField();
        TextField password = new TextField();
        TextField fullName = new TextField();
        TextField email = new TextField();
        ComboBox<UserRole> role = new ComboBox<>(FXCollections.observableArrayList(UserRole.values()));

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(fullName, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(role, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new User(username.getText(), password.getText(), role.getValue(), 
                              email.getText(), fullName.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            if (userDAO.createUser(user)) {
                loadUsers();
                showAlert("Success", "User added successfully!");
            } else {
                showAlert("Error", "Failed to add user!");
            }
        });
    }

    @FXML
    private void handleAddProject() {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add New Project");
        dialog.setHeaderText("Enter project details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField title = new TextField();
        TextArea description = new TextArea();
        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker endDate = new DatePicker(LocalDate.now().plusMonths(3));

        description.setPrefRowCount(3);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("Start Date:"), 0, 2);
        grid.add(startDate, 1, 2);
        grid.add(new Label("End Date:"), 0, 3);
        grid.add(endDate, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Project(title.getText(), description.getText(), 
                                 startDate.getValue(), endDate.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(project -> {
            if (projectDAO.createProject(project)) {
                loadProjects();
                loadProjectComboBox();
                showAlert("Success", "Project added successfully!");
            } else {
                showAlert("Error", "Failed to add project!");
            }
        });
    }

    private void handleEditUserRole(User user) {
        ChoiceDialog<UserRole> dialog = new ChoiceDialog<>(user.getRole(), UserRole.values());
        dialog.setTitle("Edit User Role");
        dialog.setHeaderText("Change role for: " + user.getFullName());
        dialog.setContentText("Select new role:");

        dialog.showAndWait().ifPresent(newRole -> {
            if (userDAO.updateUserRole(user.getId(), newRole)) {
                loadUsers();
                showAlert("Success", "User role updated successfully!");
            } else {
                showAlert("Error", "Failed to update user role!");
            }
        });
    }

    @FXML
    private void handleTestIntegration() {
        // Simulate integration test
        integrationStatusLabel.setText("Status: Testing...");
        integrationStatusLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        
        // Simulate delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    integrationStatusLabel.setText("Status: Connected ✓");
                    integrationStatusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                    showAlert("Integration Test", "Integration test successful!");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleGenerateReport() {
        generateDefaultReport();
    }

    private void generateDefaultReport() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Sample data for demonstration
        pieChartData.add(new PieChart.Data("E-Commerce Platform", 45));
        pieChartData.add(new PieChart.Data("Mobile App Development", 30));
        pieChartData.add(new PieChart.Data("Data Analytics Dashboard", 25));
        
        timeDistributionChart.setData(pieChartData);
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
}