package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Game;
import core.managers.GameManager;
import core.exceptions.TeamException;
import java.sql.SQLException;

public class ScoreKeeper extends Application {
    private Game game;
    private GameManager gameManager;
    
    public ScoreKeeper() {
        this.gameManager = new GameManager();
    }

    public void start(Stage primaryStage) {
        // Home Screen
        TextField homeTeamField = new TextField("Enter Home Team");
        TextField awayTeamField = new TextField("Enter Away Team");
        Button startGameButton = new Button("Start Game");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox homeScreen = new VBox(10, homeTeamField, awayTeamField, startGameButton, errorLabel);

        // Game Screen
        Label scoreLabel = new Label("Score: 0 - 0");
        Label periodLabel = new Label("Period: Not Started");
        TextArea eventlog = new TextArea();
        eventlog.setEditable(false);

        Button addScoreHome = new Button("Add 2 points - Home");
        Button addScoreAway = new Button("Add 2 points - Away");
        Button add3PointsHome = new Button("Add 3 points - Home");
        Button add3PointsAway = new Button("Add 3 points - Away");
        Button nextPeriodButton = new Button("Next Period");
        Button endGameButton = new Button("End Game");

        VBox gameScreen = new VBox(10, scoreLabel, periodLabel, addScoreHome, addScoreAway, 
                                   add3PointsHome, add3PointsAway, nextPeriodButton, endGameButton, eventlog);

        Scene scene = new Scene(homeScreen, 400, 300);
        primaryStage.setScene(scene);

        // Start game button - Now uses GameManager
        startGameButton.setOnAction(e -> {
            try {
                String homeTeam = homeTeamField.getText().trim();
                String awayTeam = awayTeamField.getText().trim();
                
                if (homeTeam.isEmpty() || awayTeam.isEmpty()) {
                    errorLabel.setText("Please enter both team names");
                    return;
                }
                
                // Use GameManager to setup the game
                game = gameManager.setupNewGame(homeTeam, awayTeam);
                
                periodLabel.setText("Period: " + game.getCurrentPeriod());
                updateScoreDisplay(scoreLabel);
                eventlog.appendText("Game started: " + game.getHomeTeam() + " vs " + game.getAwayTeam() + "\n");
                
                // Switch to game screen
                primaryStage.setScene(new Scene(gameScreen, 500, 400));
                
            } catch (SQLException ex) {
                errorLabel.setText("Database error: " + ex.getMessage());
            } catch (TeamException ex) {
                errorLabel.setText("Team error: " + ex.getMessage());
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Add Points Actions - Now properly differentiate between home and away
        addScoreHome.setOnAction(e -> {
            try {
                gameManager.recordHomeScore(game, "Home Player", 2);
                updateScoreDisplay(scoreLabel);
                updateEventLog(eventlog);
            } catch (Exception ex) {
                eventlog.appendText("Error recording score: " + ex.getMessage() + "\n");
            }
        });

        addScoreAway.setOnAction(e -> {
            try {
                gameManager.recordAwayScore(game, "Away Player", 2);
                updateScoreDisplay(scoreLabel);
                updateEventLog(eventlog);
            } catch (Exception ex) {
                eventlog.appendText("Error recording score: " + ex.getMessage() + "\n");
            }
        });
        
        add3PointsHome.setOnAction(e -> {
            try {
                gameManager.recordHomeScore(game, "Home Player", 3);
                updateScoreDisplay(scoreLabel);
                updateEventLog(eventlog);
            } catch (Exception ex) {
                eventlog.appendText("Error recording score: " + ex.getMessage() + "\n");
            }
        });
        
        add3PointsAway.setOnAction(e -> {
            try {
                gameManager.recordAwayScore(game, "Away Player", 3);
                updateScoreDisplay(scoreLabel);
                updateEventLog(eventlog);
            } catch (Exception ex) {
                eventlog.appendText("Error recording score: " + ex.getMessage() + "\n");
            }
        });
        
        nextPeriodButton.setOnAction(e -> {
            try {
                gameManager.advanceGamePeriod(game);
                periodLabel.setText("Period: " + game.getCurrentPeriod());
                eventlog.appendText("Period advanced to: " + game.getCurrentPeriod() + "\n");
                
                if (game.getStatus().equals("ENDED")) {
                    nextPeriodButton.setDisable(true);
                    addScoreHome.setDisable(true);
                    addScoreAway.setDisable(true);
                    add3PointsHome.setDisable(true);
                    add3PointsAway.setDisable(true);
                    eventlog.appendText("Game has ended!\n");
                }
            } catch (Exception ex) {
                eventlog.appendText("Error advancing period: " + ex.getMessage() + "\n");
            }
        });
        
        endGameButton.setOnAction(e -> {
            try {
                gameManager.endGame(game);
                nextPeriodButton.setDisable(true);
                addScoreHome.setDisable(true);
                addScoreAway.setDisable(true);
                add3PointsHome.setDisable(true);
                add3PointsAway.setDisable(true);
                endGameButton.setDisable(true);
                eventlog.appendText("Game manually ended!\n");
            } catch (Exception ex) {
                eventlog.appendText("Error ending game: " + ex.getMessage() + "\n");
            }
        });

        primaryStage.setTitle("ScoreKeeper");
        primaryStage.show();
    }
    
    private void updateScoreDisplay(Label scoreLabel) {
        scoreLabel.setText("Score: " + game.getHomeTeamStats().getScore() + " - " + game.getAwayTeamStats().getScore());
    }
    
    private void updateEventLog(TextArea eventlog) {
        // Show the last event from the game log
        if (!game.getEventLog().isEmpty()) {
            String lastEvent = game.getEventLog().get(game.getEventLog().size() - 1);
            eventlog.appendText(lastEvent + "\n");
        }
    }
}