package ui;

import database.UserDAO;
import database.Database;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginPage extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Database.initializeDatabase();
        } catch (SQLException ex) {
            System.err.println("Failed to initialize database: " + ex.getMessage());
            return;
        }

        // Create default admin user
        UserDAO adminDAO = new UserDAO();
        try {
            adminDAO.register("admin", "admin123", "admin");
        } catch (SQLException ex) {
            // Ignore if admin already exists
        }

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();

        VBox layout = new VBox(10, userLabel, userField, passLabel, passField, loginButton, registerButton, messageLabel);
        layout.setStyle("-fx-padding: 20;");

        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            UserDAO userDAO = new UserDAO();
            boolean success = false;
            try {
                success = userDAO.login(username, password);
            } catch (SQLException ex) {
                messageLabel.setText("Database error: " + ex.getMessage());
                return;
            }
            if (success) {
                messageLabel.setText("Login successful!");
                
                // Create main menu window
                Stage menuStage = new Stage();
                VBox menuLayout = new VBox(10);
                menuLayout.setStyle("-fx-padding: 20;");
                
                Button scoreKeeperButton = new Button("Score Keeper");
                Button teamManagementButton = new Button("Team Management");
                
                menuLayout.getChildren().addAll(scoreKeeperButton, teamManagementButton);
                
                scoreKeeperButton.setOnAction(event -> {
                    ScoreKeeper scoreKeeper = new ScoreKeeper();
                    try {
                        Stage scoreKeeperStage = new Stage();
                        scoreKeeper.start(scoreKeeperStage);
                    } catch (Exception ex) {
                        messageLabel.setText("Error launching Score Keeper: " + ex.getMessage());
                    }
                });
                
                teamManagementButton.setOnAction(event -> {
                    TeamManagementUI teamManagement = new TeamManagementUI();
                    Stage teamManagementStage = new Stage();
                    teamManagement.show(teamManagementStage);
                });
                
                Scene menuScene = new Scene(menuLayout, 200, 150);
                menuStage.setTitle("Basketball App Menu");
                menuStage.setScene(menuScene);
                menuStage.show();
                
                // Close the login window
                primaryStage.close();
            } else {
                messageLabel.setText("Invalid credentials.");
            }
        });

        // Register button handler
        registerButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            UserDAO userDAO = new UserDAO();
            try {
                userDAO.register(username, password, "SCORER");
                messageLabel.setText("Registration successful! You can now login.");
            } catch (IllegalArgumentException ex) {
                messageLabel.setText("Registration error: " + ex.getMessage());
            } catch (SQLException ex) {
                messageLabel.setText("Database error: " + ex.getMessage());
            }
        });

        primaryStage.setScene(new Scene(layout, 300, 250));
        primaryStage.setTitle("Basketball Scorekeeper Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}