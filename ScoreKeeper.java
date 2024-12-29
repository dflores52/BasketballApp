import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class ScoreKeeper extends Application{
    private Game game;

    @overide
    public void start(Stage primaryStage){
        //Home Screen
        TextField homeTeamField = new TextField("Enter Home Team");
        TextField awayTeamField = new TextField("Enter Away Team");
        Button startGameButton = new Button("Start Game");

        VBox homeScreen = new VBox(10, homeTeamField, awayTeamField, startGameButton);

        // Game Screen

        Label scoreLabel = new Label("Score: 0 - 0");
        Label periodLabel = new Label("Period: 0 - 0");

    }
}
