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
     * Sets up a complete game with teams and game type
     */
    public Game setupNewGame(String homeTeamName, String awayTeamName, Game.GameType gameType) 
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
        
        // Create and start game with specified type
        Game game = gameService.createGame(homeTeamName, awayTeamName, gameType);
        gameService.startGame(game);
        
        return game;
    }
    
    /**
     * Sets up a complete game with default game type (4 quarters)
     */
    public Game setupNewGame(String homeTeamName, String awayTeamName) 
            throws SQLException, TeamException {
        return setupNewGame(homeTeamName, awayTeamName, Game.GameType.FOUR_QUARTERS);
    }
    
    /**
     * Handles scoring for HOME team with specific player
     */
    public void recordHomeScore(Game game, Player player, int points) {
        gameService.recordPlayerScore(game, player, points, true);
    }
    
    /**
     * Handles scoring for AWAY team with specific player
     */
    public void recordAwayScore(Game game, Player player, int points) {
        gameService.recordPlayerScore(game, player, points, false);
    }
    
    /**
     * Records a foul for HOME team with bonus situation handling
     */
    public void recordHomeFoul(Game game, Player player) {
        gameService.recordPlayerFoul(game, player, true);
    }
    
    /**
     * Records a foul for AWAY team with bonus situation handling
     */
    public void recordAwayFoul(Game game, Player player) {
        gameService.recordPlayerFoul(game, player, false);
    }
    
    /**
     * Uses a timeout for HOME team
     */
    public boolean useHomeTimeout(Game game) {
        if (game.getHomeTeamStats().useTimeout()) {
            game.logEvent(game.getHomeTeam() + " used a timeout (Remaining: " + game.getHomeTeamStats().getTimeouts() + ")");
            return true;
        }
        return false;
    }
    
    /**
     * Uses a timeout for AWAY team
     */
    public boolean useAwayTimeout(Game game) {
        if (game.getAwayTeamStats().useTimeout()) {
            game.logEvent(game.getAwayTeam() + " used a timeout (Remaining: " + game.getAwayTeamStats().getTimeouts() + ")");
            return true;
        }
        return false;
    }
    
    /**
     * Advances the game to the next period with proper foul reset
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
    
    /**
     * Legacy methods for backward compatibility
     */
    @Deprecated
    public void recordHomeScore(Game game, String playerName, int points) {
        Player player = new Player(playerName, 0);
        recordHomeScore(game, player, points);
    }
    
    @Deprecated
    public void recordAwayScore(Game game, String playerName, int points) {
        Player player = new Player(playerName, 0);
        recordAwayScore(game, player, points);
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