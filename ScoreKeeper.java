import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;





public class ScoreKeeper extends Application{
    private Game game;


    public void start(Stage primaryStage){
        //Home Screen
        TextField homeTeamField = new TextField("Enter Home Team");
        TextField awayTeamField = new TextField("Enter Away Team");
        Button startGameButton = new Button("Start Game");

        VBox homeScreen = new VBox(10, homeTeamField, awayTeamField, startGameButton);

        // Game Screen

        Label scoreLabel = new Label("Score: 0 - 0");
        Label periodLabel = new Label("Period: Not Started");
        TextArea eventlog = new TextArea();
        eventlog.setEditable(false);

        Button addScoreHome = new Button("Add 2 points - Home");
        Button addScoreAway = new Button("Add 2 points - Away");

        VBox gameScreen = new VBox(10, scoreLabel, periodLabel,  addScoreHome, addScoreAway,eventlog);

        Scene scene = new Scene(homeScreen, 400 ,300);

        primaryStage.setScene(scene);

        // Start game button
        startGameButton.setOnAction(e -> {
            game = new Game("001", homeTeamField.getText(), awayTeamField.getText());
            game.startGame();
            periodLabel.setText("Period: 1");
            eventlog.appendText("Game started: "+ game.getHomeTeam() + " vs "+ game.getAwayTeam() + "\n");

            // Switch game to screen
            primaryStage.setScene(new Scene(gameScreen, 400, 300));
        });

        // Add Points Actions

        addScoreHome.setOnAction(e -> {
           game.getHomeTeamStats().addPoints(2);
           scoreLabel.setText("Scores: "+ game.getHomeTeamStats().getScore() + " - " + game.getAwayTeamStats().getScore());
           eventlog.appendText("Home Team scored 2 points. \n");
        });

        addScoreAway.setOnAction(e -> {
            game.getAwayTeamStats().addPoints(2);
            scoreLabel.setText("Scores: "+ game.getHomeTeamStats().getScore() + " - " + game.getAwayTeamStats().getScore());
        });
        primaryStage.setTitle("ScoreKeeper");
        primaryStage.show();




    }
}
