package com.timetracker.controller;

import com.timetracker.model.UserRole;
import com.timetracker.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private AuthenticationService authService;

    public void initialize() {
        authService = new AuthenticationService();
        
        // Add enter key support
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        if (authService.login(username, password)) {
            try {
                UserRole role = authService.getCurrentUser().getRole();
                String fxmlFile = switch (role) {
                    case ADMIN -> "/fxml/admin-dashboard.fxml";
                    case PROJECT_MANAGER -> "/fxml/pm-dashboard.fxml";
                    case TEAM_MEMBER -> "/fxml/member-dashboard.fxml";
                };

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Scene scene = new Scene(loader.load(), 1000, 700);
                
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Time Tracker - " + role.getDisplayName() + " Dashboard");
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error loading dashboard");
            }
        } else {
            showError("Invalid username or password");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}