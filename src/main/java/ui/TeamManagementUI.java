package ui;

import core.services.TeamService;
import core.services.PlayerService;
import core.exceptions.TeamException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Team;
import model.Player;

import java.sql.SQLException;

public class TeamManagementUI {
    private TeamService teamService;
    private PlayerService playerService;
    private ListView<Team> teamListView;
    private ListView<Player> playerListView;
    private TextField teamNameField;
    private TextField coachNameField;
    private TextField playerNameField;
    private TextField jerseyNumberField;
    private Label messageLabel;
    private Team selectedTeam;
    
    // Store button references
    private Button addTeamButton, updateTeamButton, deleteTeamButton;
    private Button addPlayerButton, updatePlayerButton, removePlayerButton;

    public TeamManagementUI() {
        this.teamService = new TeamService();
        this.playerService = new PlayerService();
    }

    public void show(Stage stage) {
        // Main layout using BorderPane
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Left side - Team Management
        VBox leftPanel = createTeamManagementPanel();
        leftPanel.setPrefWidth(300);
        
        // Right side - Player Management
        VBox rightPanel = createPlayerManagementPanel();
        rightPanel.setPrefWidth(300);
        
        // Center - Team Details
        VBox centerPanel = createTeamDetailsPanel();
        centerPanel.setPrefWidth(200);

        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(centerPanel);
        mainLayout.setRight(rightPanel);

        // Message label at bottom
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        mainLayout.setBottom(messageLabel);

        // Set up event handlers
        setupEventHandlers();

        // Load initial data
        refreshTeamList();

        // Set up the scene
        Scene scene = new Scene(mainLayout, 900, 700);
        stage.setTitle("Team & Player Management");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createTeamManagementPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label title = new Label("Team Management");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Team form
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(5);

        Label teamNameLabel = new Label("Team Name:");
        teamNameField = new TextField();
        Label coachNameLabel = new Label("Coach Name:");
        coachNameField = new TextField();

        formPane.addRow(0, teamNameLabel, teamNameField);
        formPane.addRow(1, coachNameLabel, coachNameField);

        // Team buttons - Store references
        addTeamButton = new Button("Add Team");
        updateTeamButton = new Button("Update Team");
        deleteTeamButton = new Button("Delete Team");
        
        HBox teamButtonBox = new HBox(5, addTeamButton, updateTeamButton, deleteTeamButton);

        // Team ListView
        teamListView = new ListView<>();
        teamListView.setPrefHeight(300);
        
        // Customize ListView cell factory
        teamListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Coach: %s) - %d players", 
                        team.getTeamName(), team.getCoachName(), team.getPlayers().size()));
                }
            }
        });

        panel.getChildren().addAll(title, formPane, teamButtonBox, teamListView);
        return panel;
    }

    private VBox createPlayerManagementPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label title = new Label("Player Management");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Player form
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(5);

        Label playerNameLabel = new Label("Player Name:");
        playerNameField = new TextField();
        Label jerseyNumberLabel = new Label("Jersey Number:");
        jerseyNumberField = new TextField();

        formPane.addRow(0, playerNameLabel, playerNameField);
        formPane.addRow(1, jerseyNumberLabel, jerseyNumberField);

        // Player buttons - Store references
        addPlayerButton = new Button("Add Player");
        updatePlayerButton = new Button("Update Player");
        removePlayerButton = new Button("Remove Player");
        
        HBox playerButtonBox = new HBox(5, addPlayerButton, updatePlayerButton, removePlayerButton);

        // Player ListView
        playerListView = new ListView<>();
        playerListView.setPrefHeight(300);
        
        // Customize Player ListView cell factory
        playerListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setText(null);
                } else {
                    setText(String.format("#%d - %s (Points: %d)", 
                        player.getJerseyNumber(), player.getPlayerName(), player.getPoints()));
                }
            }
        });

        // Initially disabled until team is selected
        enablePlayerManagement(false);

        panel.getChildren().addAll(title, formPane, playerButtonBox, playerListView);
        return panel;
    }

    private VBox createTeamDetailsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label title = new Label("Team Details");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label teamInfoLabel = new Label("Select a team to view details");
        teamInfoLabel.setWrapText(true);
        teamInfoLabel.setStyle("-fx-font-size: 12;");

        panel.getChildren().addAll(title, teamInfoLabel);
        return panel;
    }

    private void setupEventHandlers() {
        // Team selection handler
        teamListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedTeam = newVal;
            if (newVal != null) {
                teamNameField.setText(newVal.getTeamName());
                coachNameField.setText(newVal.getCoachName());
                
                // Enable player management
                enablePlayerManagement(true);
                
                // Load players for selected team
                loadPlayersForTeam(newVal);
            } else {
                enablePlayerManagement(false);
                playerListView.getItems().clear();
            }
        });

        // Player selection handler
        playerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                playerNameField.setText(newVal.getPlayerName());
                jerseyNumberField.setText(String.valueOf(newVal.getJerseyNumber()));
            }
        });

        // Team button handlers - Now using stored references
        addTeamButton.setOnAction(e -> handleAddTeam());
        updateTeamButton.setOnAction(e -> handleUpdateTeam());
        deleteTeamButton.setOnAction(e -> handleDeleteTeam());

        // Player button handlers - Now using stored references
        addPlayerButton.setOnAction(e -> handleAddPlayer());
        updatePlayerButton.setOnAction(e -> handleUpdatePlayer());
        removePlayerButton.setOnAction(e -> handleRemovePlayer());
    }

    private void enablePlayerManagement(boolean enable) {
        playerNameField.setDisable(!enable);
        jerseyNumberField.setDisable(!enable);
        addPlayerButton.setDisable(!enable);
        updatePlayerButton.setDisable(!enable);
        removePlayerButton.setDisable(!enable);
    }

    private void loadPlayersForTeam(Team team) {
        try {
            // Load players from database
            team.getPlayers().clear();
            team.getPlayers().addAll(playerService.getPlayersForTeam(team.getTeamId()));
            
            playerListView.getItems().clear();
            playerListView.getItems().addAll(team.getPlayers());
        } catch (Exception e) {
            showMessage("Error loading players: " + e.getMessage(), true);
        }
    }

    // Team management methods
    private void handleAddTeam() {
        String teamName = teamNameField.getText().trim();
        String coachName = coachNameField.getText().trim();

        try {
            teamService.createTeam(teamName, coachName);
            clearTeamFields();
            refreshTeamList();
            showMessage("Team added successfully", false);
        } catch (TeamException e) {
            showMessage("Team error: " + e.getMessage(), true);
        } catch (SQLException e) {
            showMessage("Database error: " + e.getMessage(), true);
        }
    }

    private void handleUpdateTeam() {
        if (selectedTeam == null) {
            showMessage("Please select a team to update", true);
            return;
        }

        try {
            selectedTeam.setTeamName(teamNameField.getText().trim());
            selectedTeam.setCoachName(coachNameField.getText().trim());
            teamService.updateTeam(selectedTeam);
            refreshTeamList();
            showMessage("Team updated successfully", false);
        } catch (TeamException e) {
            showMessage("Team error: " + e.getMessage(), true);
        } catch (SQLException e) {
            showMessage("Database error: " + e.getMessage(), true);
        }
    }

    private void handleDeleteTeam() {
        if (selectedTeam == null) {
            showMessage("Please select a team to delete", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Team");
        alert.setContentText("Are you sure you want to delete " + selectedTeam.getTeamName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    teamService.deleteTeam(selectedTeam.getTeamId());
                    clearTeamFields();
                    refreshTeamList();
                    showMessage("Team deleted successfully", false);
                } catch (SQLException e) {
                    showMessage("Database error: " + e.getMessage(), true);
                }
            }
        });
    }

    // Player management methods
    private void handleAddPlayer() {
        if (selectedTeam == null) {
            showMessage("Please select a team first", true);
            return;
        }

        String playerName = playerNameField.getText().trim();
        String jerseyNumberText = jerseyNumberField.getText().trim();

        try {
            int jerseyNumber = Integer.parseInt(jerseyNumberText);
            Player player = playerService.createPlayer(playerName, jerseyNumber);
            playerService.addPlayerToTeam(selectedTeam, player);
            
            clearPlayerFields();
            loadPlayersForTeam(selectedTeam);
            refreshTeamList(); // Refresh to show updated player count
            showMessage("Player added successfully", false);
        } catch (NumberFormatException e) {
            showMessage("Invalid jersey number", true);
        } catch (TeamException e) {
            showMessage("Player error: " + e.getMessage(), true);
        } catch (SQLException e) {
            showMessage("Database error: " + e.getMessage(), true);
        }
    }

    private void handleUpdatePlayer() {
        Player selectedPlayer = playerListView.getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            showMessage("Please select a player to update", true);
            return;
        }

        try {
            String newName = playerNameField.getText().trim();
            if (!newName.isEmpty()) {
                Player updatedPlayer = new Player(newName, selectedPlayer.getJerseyNumber());
                playerService.updatePlayer(updatedPlayer);
                
                loadPlayersForTeam(selectedTeam);
                showMessage("Player updated successfully", false);
            }
        } catch (TeamException e) {
            showMessage("Player error: " + e.getMessage(), true);
        } catch (SQLException e) {
            showMessage("Database error: " + e.getMessage(), true);
        }
    }

    private void handleRemovePlayer() {
        Player selectedPlayer = playerListView.getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            showMessage("Please select a player to remove", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Remove");
        alert.setHeaderText("Remove Player");
        alert.setContentText("Are you sure you want to remove " + selectedPlayer.getPlayerName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    playerService.removePlayerFromTeam(selectedPlayer, selectedTeam);
                    clearPlayerFields();
                    loadPlayersForTeam(selectedTeam);
                    refreshTeamList(); // Refresh to show updated player count
                    showMessage("Player removed successfully", false);
                } catch (SQLException e) {
                    showMessage("Database error: " + e.getMessage(), true);
                }
            }
        });
    }

    // Utility methods
    private void clearTeamFields() {
        teamNameField.clear();
        coachNameField.clear();
    }

    private void clearPlayerFields() {
        playerNameField.clear();
        jerseyNumberField.clear();
    }

    private void refreshTeamList() {
        try {
            teamListView.getItems().clear();
            teamListView.getItems().addAll(teamService.getAllTeams());
        } catch (SQLException e) {
            showMessage("Error loading teams: " + e.getMessage(), true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}