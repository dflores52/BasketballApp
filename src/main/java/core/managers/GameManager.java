package core.managers;

import core.services.GameService;
import core.services.TeamService;
import model.Game;
import model.Team;
import model.Player;
import core.exceptions.TeamException;
import java.sql.SQLException;

public class GameManager {
    private GameService gameService;
    private TeamService teamService;
    
    public GameManager() {
        this.gameService = new GameService();
        this.teamService = new TeamService();
    }
    
    /**
     * Sets up a complete game with teams
     */
    public Game setupNewGame(String homeTeamName, String awayTeamName) 
            throws SQLException, TeamException {
        
        // Validate teams exist
        Team homeTeam = findTeamByName(homeTeamName);
        Team awayTeam = findTeamByName(awayTeamName);
        
        if (homeTeam == null) {
            throw new TeamException("Home team not found: " + homeTeamName);
        }
        
        if (awayTeam == null) {
            throw new TeamException("Away team not found: " + awayTeamName);
        }
        
        // Create and start game
        Game game = gameService.createGame(homeTeamName, awayTeamName);
        gameService.startGame(game);
        
        return game;
    }
    
    /**
     * Handles scoring for HOME team
     */
    public void recordHomeScore(Game game, String playerName, int points) {
        Player player = new Player(playerName, 0);
        gameService.recordPlayerScore(game, player, points, true); // true = home team
    }
    
    /**
     * Handles scoring for AWAY team
     */
    public void recordAwayScore(Game game, String playerName, int points) {
        Player player = new Player(playerName, 0);
        gameService.recordPlayerScore(game, player, points, false); // false = away team
    }
    
    /**
     * Legacy method - keeping for compatibility but deprecated
     * @deprecated Use recordHomeScore or recordAwayScore instead
     */
    @Deprecated
    public void recordScore(Game game, String playerName, int points) {
        recordHomeScore(game, playerName, points);
    }
    
    /**
     * Advances the game to the next period
     */
    public void advanceGamePeriod(Game game) {
        gameService.advancePeriod(game);
    }
    
    /**
     * Ends the current game
     */
    public void endGame(Game game) {
        gameService.endGame(game);
    }
    
    public GameService getGameService() {
        return gameService;
    }
    
    public TeamService getTeamService() {
        return teamService;
    }
    
    private Team findTeamByName(String teamName) throws SQLException {
        return teamService.getAllTeams().stream()
            .filter(team -> team.getTeamName().equals(teamName))
            .findFirst()
            .orElse(null);
    }
}