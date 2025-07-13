package ui;

import Database.TeamDAO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Team;

import java.sql.SQLException;

public class TeamManagementUI {
    private TeamDAO teamDAO;
    private ListView<Team> teamListView;
    private TextField teamNameField;
    private TextField coachNameField;
    private Label messageLabel;

    public void show(Stage stage) {
        teamDAO = new TeamDAO();
        
        // Create the main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        // Create form elements
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(5);

        Label teamNameLabel = new Label("Team Name:");
        teamNameField = new TextField();
        Label coachNameLabel = new Label("Coach Name:");
        coachNameField = new TextField();

        formPane.addRow(0, teamNameLabel, teamNameField);
        formPane.addRow(1, coachNameLabel, coachNameField);

        // Buttons
        Button addButton = new Button("Add Team");
        Button updateButton = new Button("Update Team");
        Button deleteButton = new Button("Delete Team");
        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);

        // Team ListView
        teamListView = new ListView<>();
        teamListView.setPrefHeight(400);
        
        // Message label for feedback
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        // Add all components to main layout
        mainLayout.getChildren().addAll(formPane, buttonBox, teamListView, messageLabel);

        // Event handlers
        addButton.setOnAction(e -> handleAddTeam());
        updateButton.setOnAction(e -> handleUpdateTeam());
        deleteButton.setOnAction(e -> handleDeleteTeam());

        teamListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                teamNameField.setText(newVal.getTeamName());
                coachNameField.setText(newVal.getCoachName());
            }
        });

        // Customize ListView cell factory
        teamListView.setCellFactory(lv -> new ListCell<>() {
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

        // Load initial data
        refreshTeamList();

        // Set up the scene
        Scene scene = new Scene(mainLayout, 500, 600);
        stage.setTitle("Team Management");
        stage.setScene(scene);
        stage.show();
    }

    private void handleAddTeam() {
        String teamName = teamNameField.getText().trim();
        String coachName = coachNameField.getText().trim();

        if (teamName.isEmpty() || coachName.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        try {
            teamDAO.addTeam(teamName, coachName);
            clearFields();
            refreshTeamList();
            messageLabel.setText("Team added successfully");
        } catch (SQLException e) {
            messageLabel.setText("Error adding team: " + e.getMessage());
        }
    }

    private void handleUpdateTeam() {
        Team selectedTeam = teamListView.getSelectionModel().getSelectedItem();
        if (selectedTeam == null) {
            messageLabel.setText("Please select a team to update");
            return;
        }

        selectedTeam.setTeamName(teamNameField.getText().trim());
        selectedTeam.setCoachName(coachNameField.getText().trim());

        try {
            teamDAO.updateTeam(selectedTeam);
            refreshTeamList();
            messageLabel.setText("Team updated successfully");
        } catch (SQLException e) {
            messageLabel.setText("Error updating team: " + e.getMessage());
        }
    }

    private void handleDeleteTeam() {
        Team selectedTeam = teamListView.getSelectionModel().getSelectedItem();
        if (selectedTeam == null) {
            messageLabel.setText("Please select a team to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Team");
        alert.setContentText("Are you sure you want to delete " + selectedTeam.getTeamName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    teamDAO.deleteTeam(selectedTeam.getTeamId());
                    clearFields();
                    refreshTeamList();
                    messageLabel.setText("Team deleted successfully");
                } catch (SQLException e) {
                    messageLabel.setText("Error deleting team: " + e.getMessage());
                }
            }
        });
    }

    private void clearFields() {
        teamNameField.clear();
        coachNameField.clear();
    }

    private void refreshTeamList() {
        try {
            teamListView.getItems().clear();
            teamListView.getItems().addAll(teamDAO.getAllTeams());
        } catch (SQLException e) {
            messageLabel.setText("Error loading teams: " + e.getMessage());
        }
    }
}
