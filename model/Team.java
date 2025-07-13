package model;

import java.util.ArrayList;
import java.util.List;
public class Team {
    // Attributes
    private int teamId;
    private String teamName;
    private List<Player> players;
    private TeamStats teamStats;
    private String coachName;

    // Constructor
    public Team(int teamId, String teamName, String coachName) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.coachName = coachName;
        this.players = new ArrayList<>();
        this.teamStats = new TeamStats(teamName);
    }

    // Constructor with only team name
    public Team(String teamName) {
        this.teamId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        this.teamName = teamName;
        this.coachName = "TBD";
        this.players = new ArrayList<>();
        this.teamStats = new TeamStats(teamName);
    }


    // Add a player to the team
    public boolean addPlayer(Player player) {
        if (player == null) {
            return false;
        }

        // Check for duplicate players
        for (Player existingPlayer : players) {
            if (existingPlayer.getJerseyNumber() == player.getJerseyNumber()) {
                return false;
            }
        }

        return players.add(player);
    }

    // Get a player by their jersey number
    public Player getPlayerByNumber(int jerseyNumber) {
        for (Player player : players) {
            if (player.getJerseyNumber() == jerseyNumber) {
                return player;
            }
        }
        return null;
    }

    // Add new getter for teamId
    public int getTeamId() {
        return teamId;
    }

    // Existing getters and setters...
    public String getTeamName() {
        return teamName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public TeamStats getTeamStats() {
        return teamStats;
    }

    public String getCoachName() { return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public void setTeamName(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }
        this.teamName = teamName.trim();
    }
}