package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Game;
import model.Team;
import model.Player;
import core.managers.GameManager;
import core.services.TeamService;
import core.services.PlayerService;
import core.exceptions.TeamException;
import java.sql.SQLException;
import java.util.List;

public class ScoreKeeper extends Application {
    private Game game;
    private GameManager gameManager;
    private TeamService teamService;
    private PlayerService playerService;
    private ComboBox<Team> homeTeamCombo;
    private ComboBox<Team> awayTeamCombo;
    private ComboBox<Game.GameType> gameTypeCombo;
    private TextArea eventLogTextArea;
    
    // Game screen components
    private Label scoreLabel;
    private Label periodLabel;
    private Label homeTimeoutsLabel;
    private Label awayTimeoutsLabel;
    private Label homeFoulsLabel;
    private Label awayFoulsLabel;
    private Label homeBonusLabel;
    private Label awayBonusLabel;
    private Team homeTeam;
    private Team awayTeam;
    
    public ScoreKeeper() {
        this.gameManager = new GameManager();
        this.teamService = new TeamService();
        this.playerService = new PlayerService();
    }

    public void start(Stage primaryStage) {
        VBox setupScreen = createGameSetupScreen(primaryStage);
        Scene scene = new Scene(setupScreen, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Basketball ScoreKeeper - Game Setup");
        primaryStage.show();
    }

    private VBox createGameSetupScreen(Stage primaryStage) {
        VBox setupScreen = new VBox(15);
        setupScreen.setPadding(new Insets(20));
        setupScreen.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Basketball Game Setup");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox teamSelectionBox = createTeamSelectionSection();
        VBox gameOptionsBox = createGameOptionsSection();
        HBox buttonBox = createControlButtons(primaryStage);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
        
        setupScreen.getChildren().addAll(
            titleLabel,
            new Separator(),
            teamSelectionBox,
            new Separator(),
            gameOptionsBox,
            new Separator(),
            buttonBox,
            errorLabel
        );
        
        setupScreen.setUserData(errorLabel);
        return setupScreen;
    }

    private VBox createTeamSelectionSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label sectionTitle = new Label("Team Selection");
        sectionTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Home team selection
        HBox homeTeamBox = new HBox(10);
        homeTeamBox.setAlignment(Pos.CENTER_LEFT);
        Label homeLabel = new Label("Home Team:");
        homeLabel.setMinWidth(100);
        homeTeamCombo = new ComboBox<>();
        homeTeamCombo.setPromptText("Select Home Team");
        homeTeamCombo.setPrefWidth(200);
        Button createHomeTeamBtn = new Button("Create New");
        createHomeTeamBtn.setOnAction(e -> openCreateTeamDialog(homeTeamCombo));
        homeTeamBox.getChildren().addAll(homeLabel, homeTeamCombo, createHomeTeamBtn);

        // Away team selection
        HBox awayTeamBox = new HBox(10);
        awayTeamBox.setAlignment(Pos.CENTER_LEFT);
        Label awayLabel = new Label("Away Team:");
        awayLabel.setMinWidth(100);
        awayTeamCombo = new ComboBox<>();
        awayTeamCombo.setPromptText("Select Away Team");
        awayTeamCombo.setPrefWidth(200);
        Button createAwayTeamBtn = new Button("Create New");
        createAwayTeamBtn.setOnAction(e -> openCreateTeamDialog(awayTeamCombo));
        awayTeamBox.getChildren().addAll(awayLabel, awayTeamCombo, createAwayTeamBtn);

        Button refreshBtn = new Button("Refresh Teams");
        refreshBtn.setOnAction(e -> loadTeams());
        
        setupTeamComboBoxes();
        loadTeams();

        section.getChildren().addAll(sectionTitle, homeTeamBox, awayTeamBox, refreshBtn);
        return section;
    }

    private VBox createGameOptionsSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        Label sectionTitle = new Label("Game Options");
        sectionTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Game type selection
        HBox gameTypeBox = new HBox(10);
        gameTypeBox.setAlignment(Pos.CENTER_LEFT);
        Label gameTypeLabel = new Label("Game Type:");
        gameTypeLabel.setMinWidth(100);
        gameTypeCombo = new ComboBox<>();
        gameTypeCombo.getItems().addAll(Game.GameType.values());
        gameTypeCombo.setValue(Game.GameType.FOUR_QUARTERS);
        gameTypeCombo.setPrefWidth(200);
        
        // Custom cell factory for game type combo
        gameTypeCombo.setCellFactory(lv -> new ListCell<Game.GameType>() {
            @Override
            protected void updateItem(Game.GameType gameType, boolean empty) {
                super.updateItem(gameType, empty);
                if (empty || gameType == null) {
                    setText(null);
                } else {
                    setText(getGameTypeDisplayName(gameType));
                }
            }
        });
        
        gameTypeCombo.setButtonCell(new ListCell<Game.GameType>() {
            @Override
            protected void updateItem(Game.GameType gameType, boolean empty) {
                super.updateItem(gameType, empty);
                if (empty || gameType == null) {
                    setText("Select Game Type");
                } else {
                    setText(getGameTypeDisplayName(gameType));
                }
            }
        });
        
        gameTypeBox.getChildren().addAll(gameTypeLabel, gameTypeCombo);

        // Bonus information display
        Label bonusInfoLabel = new Label();
        bonusInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        bonusInfoLabel.setWrapText(true);
        
        // Update bonus info when game type changes
        gameTypeCombo.setOnAction(e -> {
            Game.GameType selectedType = gameTypeCombo.getValue();
            if (selectedType != null) {
                bonusInfoLabel.setText(getBonusInfoText(selectedType));
            }
        });
        
        // Set initial bonus info
        bonusInfoLabel.setText(getBonusInfoText(Game.GameType.FOUR_QUARTERS));

        section.getChildren().addAll(sectionTitle, gameTypeBox, bonusInfoLabel);
        return section;
    }

    private String getGameTypeDisplayName(Game.GameType gameType) {
        switch (gameType) {
            case FOUR_QUARTERS:
                return "4 Quarters (High School/College)";
            case TWO_HALVES:
                return "2 Halves (Professional)";
            default:
                return gameType.name().replace("_", " ");
        }
    }

    private String getBonusInfoText(Game.GameType gameType) {
        switch (gameType) {
            case FOUR_QUARTERS:
                return "Bonus Rules: Team fouls reset each quarter. Bonus at 5 fouls per quarter.";
            case TWO_HALVES:
                return "Bonus Rules: Team fouls reset each half. Bonus at 7 fouls, Double Bonus at 10 fouls per half.";
            default:
                return "";
        }
    }

    private HBox createControlButtons(Stage primaryStage) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button startGameBtn = new Button("Start Game");
        startGameBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20;");
        startGameBtn.setOnAction(e -> startGame(primaryStage));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20;");
        cancelBtn.setOnAction(e -> primaryStage.close());

        buttonBox.getChildren().addAll(startGameBtn, cancelBtn);
        return buttonBox;
    }

    private void setupTeamComboBoxes() {
        homeTeamCombo.setCellFactory(lv -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Coach: %s)", team.getTeamName(), team.getCoachName()));
                }
            }
        });

        awayTeamCombo.setCellFactory(lv -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Coach: %s)", team.getTeamName(), team.getCoachName()));
                }
            }
        });

        homeTeamCombo.setButtonCell(new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText("Select Home Team");
                } else {
                    setText(team.getTeamName());
                }
            }
        });

        awayTeamCombo.setButtonCell(new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText("Select Away Team");
                } else {
                    setText(team.getTeamName());
                }
            }
        });
    }

    private void loadTeams() {
        try {
            List<Team> teams = teamService.getAllTeams();
            homeTeamCombo.getItems().clear();
            awayTeamCombo.getItems().clear();
            homeTeamCombo.getItems().addAll(teams);
            awayTeamCombo.getItems().addAll(teams);
        } catch (SQLException e) {
            showError("Error loading teams: " + e.getMessage());
        }
    }

    private void openCreateTeamDialog(ComboBox<Team> targetCombo) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New Team");
        dialog.setResizable(false);

        VBox dialogBox = new VBox(15);
        dialogBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Create New Team");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        Label teamNameLabel = new Label("Team Name:");
        TextField teamNameField = new TextField();
        teamNameField.setPromptText("Enter team name");
        teamNameField.setPrefWidth(200);

        Label coachNameLabel = new Label("Coach Name:");
        TextField coachNameField = new TextField();
        coachNameField.setPromptText("Enter coach name");
        coachNameField.setPrefWidth(200);

        formGrid.addRow(0, teamNameLabel, teamNameField);
        formGrid.addRow(1, coachNameLabel, coachNameField);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button createBtn = new Button("Create Team");
        createBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(createBtn, cancelBtn);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        dialogBox.getChildren().addAll(titleLabel, formGrid, buttonBox, errorLabel);

        createBtn.setOnAction(e -> {
            String teamName = teamNameField.getText().trim();
            String coachName = coachNameField.getText().trim();

            if (teamName.isEmpty() || coachName.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                return;
            }

            try {
                Team newTeam = teamService.createTeam(teamName, coachName);
                loadTeams();
                targetCombo.setValue(newTeam);
                dialog.close();
            } catch (TeamException ex) {
                errorLabel.setText("Team error: " + ex.getMessage());
            } catch (SQLException ex) {
                errorLabel.setText("Database error: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        Scene dialogScene = new Scene(dialogBox, 350, 250);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void startGame(Stage primaryStage) {
        homeTeam = homeTeamCombo.getValue();
        awayTeam = awayTeamCombo.getValue();
        Game.GameType selectedGameType = gameTypeCombo.getValue();

        if (homeTeam == null || awayTeam == null) {
            showError("Please select both teams");
            return;
        }

        if (homeTeam.equals(awayTeam)) {
            showError("Home and Away teams cannot be the same");
            return;
        }

        try {
            homeTeam.getPlayers().clear();
            homeTeam.getPlayers().addAll(playerService.getPlayersForTeam(homeTeam.getTeamId()));
            
            awayTeam.getPlayers().clear();
            awayTeam.getPlayers().addAll(playerService.getPlayersForTeam(awayTeam.getTeamId()));
            
            game = gameManager.setupNewGame(homeTeam.getTeamName(), awayTeam.getTeamName(), selectedGameType);
            
            VBox gameScreen = createGameScreen(primaryStage);
            Scene gameScene = new Scene(gameScreen, 1000, 800);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Basketball ScoreKeeper - Game in Progress");
            
            addEventToLog("Game started: " + homeTeam.getTeamName() + " vs " + awayTeam.getTeamName());
            
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        } catch (TeamException ex) {
            showError("Team error: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private VBox createGameScreen(Stage primaryStage) {
        VBox gameScreen = new VBox(15);
        gameScreen.setPadding(new Insets(15));
        gameScreen.setStyle("-fx-background-color: #f0f0f0;");

        VBox headerBox = createEnhancedGameHeader();
        HBox actionBox = createActionButtons();
        VBox eventLogBox = createEventLogSection();

        gameScreen.getChildren().addAll(headerBox, actionBox, eventLogBox);
        return gameScreen;
    }

    private VBox createEnhancedGameHeader() {
        VBox headerBox = new VBox(10);
        headerBox.setStyle("-fx-background-color: #2196F3; -fx-padding: 20; -fx-background-radius: 10;");

        // Main score display
        HBox scoreBox = new HBox(30);
        scoreBox.setAlignment(Pos.CENTER);

        Label homeTeamLabel = new Label(homeTeam.getTeamName());
        homeTeamLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: white;");

        scoreLabel = new Label("0 - 0");
        scoreLabel.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: white;");

        Label awayTeamLabel = new Label(awayTeam.getTeamName());
        awayTeamLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: white;");

        scoreBox.getChildren().addAll(homeTeamLabel, scoreLabel, awayTeamLabel);

        // Game status row
        HBox statusBox = new HBox(40);
        statusBox.setAlignment(Pos.CENTER);

        String periodName = game.getGameType() == Game.GameType.FOUR_QUARTERS ? "Quarter" : "Half";
        periodLabel = new Label(periodName + ": " + game.getCurrentPeriod());
        periodLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");

        Label gameStatusLabel = new Label("Status: " + game.getStatus());
        gameStatusLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");

        statusBox.getChildren().addAll(periodLabel, gameStatusLabel);

        // Team stats row with bonus information
        HBox teamStatsBox = new HBox(50);
        teamStatsBox.setAlignment(Pos.CENTER);

        // Home team stats
        VBox homeStatsBox = new VBox(5);
        homeStatsBox.setAlignment(Pos.CENTER);
        Label homeStatsTitle = new Label(homeTeam.getTeamName() + " Stats");
        homeStatsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white;");

        homeFoulsLabel = new Label("Fouls: 0");
        homeFoulsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");

        homeTimeoutsLabel = new Label("Timeouts: 7");
        homeTimeoutsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");

        homeBonusLabel = new Label("");
        homeBonusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        homeStatsBox.getChildren().addAll(homeStatsTitle, homeFoulsLabel, homeTimeoutsLabel, homeBonusLabel);

        // Away team stats
        VBox awayStatsBox = new VBox(5);
        awayStatsBox.setAlignment(Pos.CENTER);
        Label awayStatsTitle = new Label(awayTeam.getTeamName() + " Stats");
        awayStatsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white;");

        awayFoulsLabel = new Label("Fouls: 0");
        awayFoulsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");

        awayTimeoutsLabel = new Label("Timeouts: 7");
        awayTimeoutsLabel.setStyle("-fx-font-size: 12; -fx-text-fill: white;");

        awayBonusLabel = new Label("");
        awayBonusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        awayStatsBox.getChildren().addAll(awayStatsTitle, awayFoulsLabel, awayTimeoutsLabel, awayBonusLabel);

        teamStatsBox.getChildren().addAll(homeStatsBox, awayStatsBox);

        headerBox.getChildren().addAll(scoreBox, statusBox, teamStatsBox);
        return headerBox;
    }

    private HBox createActionButtons() {
        HBox actionBox = new HBox(20);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(20));

        VBox homeActionsBox = createTeamActionButtons(homeTeam, true);
        VBox controlBox = createGameControlButtons();
        VBox awayActionsBox = createTeamActionButtons(awayTeam, false);

        actionBox.getChildren().addAll(homeActionsBox, controlBox, awayActionsBox);
        return actionBox;
    }

    private VBox createTeamActionButtons(Team team, boolean isHomeTeam) {
        VBox teamBox = new VBox(10);
        teamBox.setAlignment(Pos.CENTER);
        teamBox.setStyle("-fx-border-color: " + (isHomeTeam ? "#4CAF50" : "#f44336") + 
                        "; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 15;");

        Label teamLabel = new Label(team.getTeamName() + (isHomeTeam ? " (Home)" : " (Away)"));
        teamLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + 
                          (isHomeTeam ? "#4CAF50" : "#f44336") + ";");

        // Player selection
        VBox playerScoringBox = new VBox(5);
        playerScoringBox.setAlignment(Pos.CENTER);
        
        Label playerLabel = new Label("Select Player:");
        playerLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        ComboBox<Player> playerCombo = new ComboBox<>();
        playerCombo.setPromptText("Select Player");
        playerCombo.setPrefWidth(150);
        playerCombo.getItems().addAll(team.getPlayers());
        
        playerCombo.setCellFactory(lv -> new ListCell<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setText(null);
                } else {
                    setText(String.format("#%d - %s", player.getJerseyNumber(), player.getPlayerName()));
                }
            }
        });
        
        playerCombo.setButtonCell(new ListCell<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setText("Select Player");
                } else {
                    setText(String.format("#%d - %s", player.getJerseyNumber(), player.getPlayerName()));
                }
            }
        });

        playerScoringBox.getChildren().addAll(playerLabel, playerCombo);

        // Scoring buttons
        HBox scoreButtonsBox = new HBox(5);
        scoreButtonsBox.setAlignment(Pos.CENTER);
        
        Button score1Btn = new Button("1");
        Button score2Btn = new Button("2");
        Button score3Btn = new Button("3");
        
        String buttonStyle = "-fx-background-color: " + (isHomeTeam ? "#4CAF50" : "#f44336") + 
                            "; -fx-text-fill: white; -fx-pref-width: 40; -fx-pref-height: 40;";
        score1Btn.setStyle(buttonStyle);
        score2Btn.setStyle(buttonStyle);
        score3Btn.setStyle(buttonStyle);
        
        score1Btn.setOnAction(e -> scoreWithPlayer(playerCombo.getValue(), 1, isHomeTeam));
        score2Btn.setOnAction(e -> scoreWithPlayer(playerCombo.getValue(), 2, isHomeTeam));
        score3Btn.setOnAction(e -> scoreWithPlayer(playerCombo.getValue(), 3, isHomeTeam));
        
        scoreButtonsBox.getChildren().addAll(score1Btn, score2Btn, score3Btn);

        // Other actions
        Button foulBtn = new Button("Foul");
        foulBtn.setStyle(buttonStyle);
        foulBtn.setOnAction(e -> recordFoul(playerCombo.getValue(), isHomeTeam));
        
        Button timeoutBtn = new Button("Timeout");
        timeoutBtn.setStyle(buttonStyle);
        timeoutBtn.setOnAction(e -> useTimeout(isHomeTeam));

        teamBox.getChildren().addAll(teamLabel, playerScoringBox, scoreButtonsBox, foulBtn, timeoutBtn);
        return teamBox;
    }

    private VBox createGameControlButtons() {
        VBox controlBox = new VBox(10);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setStyle("-fx-border-color: #FF9800; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 15;");

        Label controlLabel = new Label("Game Control");
        controlLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #FF9800;");

        String periodName = game.getGameType() == Game.GameType.FOUR_QUARTERS ? "Quarter" : "Half";
        Button nextPeriodBtn = new Button("Next " + periodName);
        nextPeriodBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-pref-width: 120;");
        nextPeriodBtn.setOnAction(e -> advancePeriod());

        Button endGameBtn = new Button("End Game");
        endGameBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-pref-width: 120;");
        endGameBtn.setOnAction(e -> endGame());

        controlBox.getChildren().addAll(controlLabel, nextPeriodBtn, endGameBtn);
        return controlBox;
    }

    private VBox createEventLogSection() {
        VBox eventLogBox = new VBox(5);
        eventLogBox.setPadding(new Insets(0, 20, 20, 20));
        
        Label logLabel = new Label("Event Log:");
        logLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        eventLogTextArea = new TextArea();
        eventLogTextArea.setEditable(false);
        eventLogTextArea.setPrefHeight(200);
        eventLogTextArea.setStyle("-fx-control-inner-background: #f9f9f9;");
        eventLogTextArea.setWrapText(true);

        eventLogBox.getChildren().addAll(logLabel, eventLogTextArea);
        return eventLogBox;
    }

    private void scoreWithPlayer(Player player, int points, boolean isHomeTeam) {
        if (player == null) {
            showError("Please select a player first");
            return;
        }

        try {
            if (isHomeTeam) {
                gameManager.recordHomeScore(game, player, points);
            } else {
                gameManager.recordAwayScore(game, player, points);
            }
            
            updateAllDisplays();
            
        } catch (Exception ex) {
            showError("Error recording score: " + ex.getMessage());
        }
    }

    private void recordFoul(Player player, boolean isHomeTeam) {
        if (player == null) {
            showError("Please select a player first");
            return;
        }

        try {
            if (isHomeTeam) {
                gameManager.recordHomeFoul(game, player);
            } else {
                gameManager.recordAwayFoul(game, player);
            }
            
            updateAllDisplays();
            
        } catch (Exception ex) {
            showError("Error recording foul: " + ex.getMessage());
        }
    }

    private void useTimeout(boolean isHomeTeam) {
        try {
            boolean success;
            if (isHomeTeam) {
                success = gameManager.useHomeTimeout(game);
            } else {
                success = gameManager.useAwayTimeout(game);
            }
            
            if (!success) {
                showError((isHomeTeam ? "Home" : "Away") + " team has no timeouts remaining");
            }
            
            updateAllDisplays();
            
        } catch (Exception ex) {
            showError("Error using timeout: " + ex.getMessage());
        }
    }

    private void advancePeriod() {
        try {
            gameManager.advanceGamePeriod(game);
            updateAllDisplays();
            
            if (game.getStatus().equals("ENDED")) {
                showGameEndedDialog();
            }
            
        } catch (Exception ex) {
            showError("Error advancing period: " + ex.getMessage());
        }
    }

    private void endGame() {
        try {
            gameManager.endGame(game);
            updateAllDisplays();
            showGameEndedDialog();
            
        } catch (Exception ex) {
            showError("Error ending game: " + ex.getMessage());
        }
    }

    private void updateAllDisplays() {
        // Update score
        scoreLabel.setText(game.getHomeTeamStats().getScore() + " - " + game.getAwayTeamStats().getScore());
        
        // Update period
        String periodName = game.getGameType() == Game.GameType.FOUR_QUARTERS ? "Quarter" : "Half";
        periodLabel.setText(periodName + ": " + game.getCurrentPeriod());
        
        // Update team stats
        homeFoulsLabel.setText("Fouls: " + game.getHomeTeamStats().getFouls());
        homeTimeoutsLabel.setText("Timeouts: " + game.getHomeTeamStats().getTimeouts());
        
        awayFoulsLabel.setText("Fouls: " + game.getAwayTeamStats().getFouls());
        awayTimeoutsLabel.setText("Timeouts: " + game.getAwayTeamStats().getTimeouts());
        
        // Update bonus status
        String homeBonusStatus = game.getHomeBonusStatus();
        homeBonusLabel.setText(homeBonusStatus.isEmpty() ? "" : "OPPONENT IN " + homeBonusStatus);
        
        String awayBonusStatus = game.getAwayBonusStatus();
        awayBonusLabel.setText(awayBonusStatus.isEmpty() ? "" : "OPPONENT IN " + awayBonusStatus);
        
        // Update event log
        updateEventLog();
    }

    private void updateEventLog() {
        if (!game.getEventLog().isEmpty()) {
            String lastEvent = game.getEventLog().get(game.getEventLog().size() - 1);
            addEventToLog(lastEvent);
        }
    }

    private void addEventToLog(String event) {
        if (eventLogTextArea != null) {
            String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String logEntry = String.format("[%s] %s\n", timestamp, event);
            eventLogTextArea.appendText(logEntry);
            eventLogTextArea.setScrollTop(Double.MAX_VALUE);
        }
    }

    private void showGameEndedDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Ended");
        alert.setHeaderText("Game has ended!");
        alert.setContentText(String.format("Final Score: %s %d - %d %s\n\nFinal Stats:\n%s: %d fouls, %d timeouts remaining\n%s: %d fouls, %d timeouts remaining",
            game.getHomeTeam(), game.getHomeTeamStats().getScore(),
            game.getAwayTeamStats().getScore(), game.getAwayTeam(),
            game.getHomeTeam(), game.getHomeTeamStats().getFouls(), game.getHomeTeamStats().getTimeouts(),
            game.getAwayTeam(), game.getAwayTeamStats().getFouls(), game.getAwayTeamStats().getTimeouts()));
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}