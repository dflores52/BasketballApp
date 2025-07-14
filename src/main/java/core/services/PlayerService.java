package core.services;

import model.Player;
import model.Team;
import core.exceptions.TeamException;
import database.PlayerDAO;
import java.sql.SQLException;
import java.util.List;

public class PlayerService {
    private PlayerDAO playerDAO;
    
    public PlayerService() {
        this.playerDAO = new PlayerDAO();
    }
    
    /**
     * Creates a new player with validation
     */
    public Player createPlayer(String playerName, int jerseyNumber) throws TeamException {
        // Validation
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new TeamException("Player name cannot be empty");
        }
        
        if (jerseyNumber < 0 || jerseyNumber > 99) {
            throw new TeamException("Jersey number must be between 0 and 99");
        }
        
        return new Player(playerName.trim(), jerseyNumber);
    }
    
    /**
     * Adds a player to a team with validation
     */
    public void addPlayerToTeam(Team team, Player player) throws TeamException, SQLException {
        // Validation through TeamService
        TeamService teamService = new TeamService();
        teamService.addPlayerToTeam(team, player);
        
        // Save to database if you have PlayerDAO
        try {
            playerDAO.addPlayer(player, team.getTeamId());
        } catch (SQLException e) {
            throw new SQLException("Failed to save player to database: " + e.getMessage());
        }
    }
    
    /**
     * Gets all players for a team
     */
    public List<Player> getPlayersForTeam(int teamId) throws SQLException {
        return playerDAO.getPlayersByTeamId(teamId);
    }
    
    /**
     * Updates a player's information
     */
    public void updatePlayer(Player player) throws TeamException, SQLException {
        if (player == null) {
            throw new TeamException("Player cannot be null");
        }
        
        if (player.getPlayerName() == null || player.getPlayerName().trim().isEmpty()) {
            throw new TeamException("Player name cannot be empty");
        }
        
        playerDAO.updatePlayer(player);
    }
    
    /**
     * Removes a player from a team
     */
    public void removePlayerFromTeam(Player player, Team team) throws SQLException {
        team.getPlayers().remove(player);
        playerDAO.deletePlayer(player.getJerseyNumber(), team.getTeamId());
    }
}
