package core.services;

import model.Game;
import model.Player;
import java.util.List;

public class GameService {
    
    public GameService() {
        // Constructor
    }
    
    /**
     * Creates a new game with default game type (4 quarters)
     */
    public Game createGame(String homeTeam, String awayTeam) {
        String gameId = generateGameId();
        Game game = new Game(gameId, homeTeam, awayTeam);
        return game;
    }
    
    /**
     * Creates a new game with specified game type
     */
    public Game createGame(String homeTeam, String awayTeam, Game.GameType gameType) {
        String gameId = generateGameId();
        Game game = new Game(gameId, homeTeam, awayTeam, gameType);
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
        String eventMessage = String.format("#%d %s (%s) scored %d points", 
            player.getJerseyNumber(), player.getPlayerName(), teamName, points);
        game.logEvent(eventMessage);
    }
    
    /**
     * Handles player fouls with bonus situation checking
     */
    public void recordPlayerFoul(Game game, Player player, boolean isHomeTeam) {
        if (game.getStatus().equals("ENDED")) {
            throw new IllegalStateException("Cannot record foul in ended game");
        }
        
        // Add foul to player and team
        player.addFoul();
        
        String teamName = isHomeTeam ? game.getHomeTeam() : game.getAwayTeam();
        String opponentName = isHomeTeam ? game.getAwayTeam() : game.getHomeTeam();
        
        if (isHomeTeam) {
            game.getHomeTeamStats().addFoul();
        } else {
            game.getAwayTeamStats().addFoul();
        }
        
        // Create foul event message
        String foulMessage = String.format("#%d %s (%s) committed a foul", 
            player.getJerseyNumber(), player.getPlayerName(), teamName);
        
        // Check for bonus situation
        boolean opponentInBonus = isHomeTeam ? game.isAwayTeamInBonus() : game.isHomeTeamInBonus();
        boolean opponentInDoubleBonus = isHomeTeam ? game.isAwayTeamInDoubleBonus() : game.isHomeTeamInDoubleBonus();
        
        if (opponentInDoubleBonus) {
            foulMessage += " - " + opponentName + " in DOUBLE BONUS";
        } else if (opponentInBonus) {
            foulMessage += " - " + opponentName + " in BONUS";
        }
        
        game.logEvent(foulMessage);
    }
    
    /**
     * Handles game period management with proper foul reset
     */
    public void advancePeriod(Game game) {
        if (game.getCurrentPeriod() >= game.getMaxPeriods()) {
            game.endGame();
        } else {
            game.endPeriod();
            game.startPeriod();
        }
    }
    
    /**
     * Starts a game
     */
    public void startGame(Game game) {
        game.startGame();
    }
    
    /**
     * Ends a game
     */
    public void endGame(Game game) {
        game.endGame();
    }
    
    /**
     * Legacy method for backward compatibility
     * @deprecated Use recordPlayerScore(Game, Player, int, boolean) instead
     */
    @Deprecated
    public void recordPlayerScore(Game game, Player player, int points) {
        recordPlayerScore(game, player, points, true); // Default to home team
    }
    
    private String generateGameId() {
        return "GAME_" + System.currentTimeMillis();
    }
}