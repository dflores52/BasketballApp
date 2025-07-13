package ui;

import Database.UserDAO;
import Database.Database;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginPage extends Application {
    private PageManager pageManager = PageManager.getInstance();

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
        Button registerButton = new Button("Register"); // New register button
        Label messageLabel = new Label();

        VBox layout = new VBox(10, userLabel, userField, passLabel, passField, loginButton, registerButton, messageLabel);
        layout.setStyle("-fx-padding: 20;");

        // Existing login button handler
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
                // Create and show the ui.ScoreKeeper window
                ScoreKeeper scoreKeeper = new ScoreKeeper();
                try {
                    Stage scoreKeeperStage = new Stage();
                    scoreKeeper.start(scoreKeeperStage);
                    // Close the login window
                    primaryStage.close();
                } catch (Exception ex) {
                    messageLabel.setText("Error launching application: " + ex.getMessage());
                }
            } else {
                messageLabel.setText("Invalid credentials.");
            }
        });

        // New register button handler
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