package ui;

import javafx.stage.Stage;
import java.util.Stack;
import javafx.scene.Scene;

public class PageManager {
    private static PageManager instance;
    private Stage primaryStage;
    private Stack<Scene> navigationStack = new Stack<>();
    private String currentUser;
    private String userRole;
    
    private PageManager() {}
    
    public static PageManager getInstance() {
        if (instance == null) {
            instance = new PageManager();
        }
        return instance;
    }
    
    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginPage();
    }
    
    public void setUserSession(String username, String role) {
        this.currentUser = username;
        this.userRole = role;
    }
    
    public void showLoginPage() {
        LoginPage loginPage = new LoginPage();
        Scene scene = loginPage.createScene();
        setScene(scene);
    }
    
    public void showScoreKeeper() {
        if (currentUser == null) {
            showLoginPage();
            return;
        }
        ScoreKeeper scoreKeeper = new ScoreKeeper();
        Scene scene = scoreKeeper.createScene();
        setScene(scene);
    }
    
    public void showTeamManagement() {
        if (currentUser == null || !userRole.equals("admin")) {
            showLoginPage();
            return;
        }
        TeamManagementUI teamManagement = new TeamManagementUI();
        Scene scene = teamManagement.createScene();
        setScene(scene);
    }
    
    private void setScene(Scene scene) {
        navigationStack.push(scene);
        primaryStage.setScene(scene);
    }
    
    public void navigateBack() {
        if (navigationStack.size() > 1) {
            navigationStack.pop(); // Remove current scene
            Scene previousScene = navigationStack.peek();
            primaryStage.setScene(previousScene);
        }
    }
}
