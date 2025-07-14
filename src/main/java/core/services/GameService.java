package core.services;

import model.Game;
import model.Player;
import java.util.List;

public class GameService {
    
    public GameService() {
        // Constructor
    }
    
    /**
     * Creates a new game
     */
    public Game createGame(String homeTeam, String awayTeam) {
        String gameId = generateGameId();
        Game game = new Game(gameId, homeTeam, awayTeam);
        return game;
    }
    
    /**
     * Handles player scoring with team differentiation
     */
    public void recordPlayerScore(Game game, Player player, int points, boolean isHomeTeam) {
        // Business logic validation
        if (points < 0 || points > 3) {
            throw new IllegalArgumentException("Invalid points value: " + points);
        }
        
        if (game.getStatus().equals("ENDED")) {
            throw new IllegalStateException("Cannot score in ended game");
        }
        
        // Record the score to the correct team
        if (isHomeTeam) {
            game.getHomeTeamStats().addPoints(points);
        } else {
            game.getAwayTeamStats().addPoints(points);
        }
        
        // Log the event
        String teamName = isHomeTeam ? game.getHomeTeam() : game.getAwayTeam();
        String eventMessage = String.format("%s (%s) scored %d points", 
            player.getPlayerName(), teamName, points);
        game.logEvent(eventMessage);
    }
    
    /**
     * Legacy method for backward compatibility
     * @deprecated Use recordPlayerScore(Game, Player, int, boolean) instead
     */
    @Deprecated
    public void recordPlayerScore(Game game, Player player, int points) {
        recordPlayerScore(game, player, points, true); // Default to home team
    }
    
    /**
     * Handles game period management
     */
    public void advancePeriod(Game game) {
        if (game.getCurrentPeriod() >= 4) {
            game.endGame();
        } else {
            game.startPeriod();
        }
    }
    
    /**
     * Starts a game
     */
    public void startGame(Game game) {
        game.startGame();
        game.logEvent("Game started between " + game.getHomeTeam() + " and " + game.getAwayTeam());
    }
    
    /**
     * Ends a game
     */
    public void endGame(Game game) {
        game.endGame();
        game.logEvent("Game ended");
    }
    
    private String generateGameId() {
        return "GAME_" + System.currentTimeMillis();
    }
}