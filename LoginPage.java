import Database.UserDAO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginPage extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Button loginButton = new Button("Login");
        Label messageLabel = new Label();

        VBox layout = new VBox(10, userLabel, userField, passLabel, passField, loginButton, messageLabel);
        layout.setStyle("-fx-padding: 20;");

        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            UserDAO userDAO = new UserDAO();
            boolean success = false;
            try {
                success = userDAO.login(username, password);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            if (success) {
                messageLabel.setText("Login successful!");
                // You can open the main app window here
            } else {
                messageLabel.setText("Invalid credentials.");
            }
        });

        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.setTitle("Basketball Scorekeeper Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
