package model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String gameId;
    private String homeTeam;
    private String awayTeam;
    private TeamStats homeTeamStats;
    private TeamStats awayTeamStats;
    private int currentPeriod;
    private String status;
    private List<String> eventLog;
    private GameType gameType;
    private int maxPeriods;

    public enum GameType {
        FOUR_QUARTERS(4, 5, 0, 0),  // 4 quarters, bonus at 5 fouls
        TWO_HALVES(2, 7, 10, 0);    // 2 halves, bonus at 7, double bonus at 10

        private final int periods;
        private final int bonusLimit;
        private final int doubleBonusLimit;
        private final int technicalFoulLimit;

        GameType(int periods, int bonusLimit, int doubleBonusLimit, int technicalFoulLimit) {
            this.periods = periods;
            this.bonusLimit = bonusLimit;
            this.doubleBonusLimit = doubleBonusLimit;
            this.technicalFoulLimit = technicalFoulLimit;
        }

        public int getPeriods() { return periods; }
        public int getBonusLimit() { return bonusLimit; }
        public int getDoubleBonusLimit() { return doubleBonusLimit; }
        public int getTechnicalFoulLimit() { return technicalFoulLimit; }
    }

    // Constructor
    public Game(String gameId, String homeTeam, String awayTeam) {
        this.gameId = gameId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamStats = new TeamStats(homeTeam);
        this.awayTeamStats = new TeamStats(awayTeam);
        this.currentPeriod = 0;
        this.status = "NOT_STARTED";
        this.eventLog = new ArrayList<>();
        this.gameType = GameType.FOUR_QUARTERS; // Default
        this.maxPeriods = gameType.getPeriods();
    }

    // Constructor with game type
    public Game(String gameId, String homeTeam, String awayTeam, GameType gameType) {
        this(gameId, homeTeam, awayTeam);
        this.gameType = gameType;
        this.maxPeriods = gameType.getPeriods();
    }

    // Game control methods
    public void startGame() {
        this.status = "IN_PROGRESS";
        this.currentPeriod = 1;
        resetPeriodFouls();
        logEvent("Game started - " + gameType.name().replace("_", " "));
    }

    public void startPeriod() {
        if (currentPeriod < maxPeriods) {
            currentPeriod++;
            resetPeriodFouls();
            logEvent("Period " + currentPeriod + " started - Team fouls reset");
        } else {
            endGame();
        }
    }

    public void endPeriod() {
        logEvent("Period " + currentPeriod + " ended");
    }

    public void endGame() {
        this.status = "ENDED";
        logEvent("Game ended");
    }

    // Reset team fouls at the start of each period
    private void resetPeriodFouls() {
        homeTeamStats.resetPeriodFouls();
        awayTeamStats.resetPeriodFouls();
    }

    // Add an event to the log
    public void logEvent(String event) {
        this.eventLog.add(event);
        System.out.println(event);
    }

    // Bonus situation methods
    public boolean isHomeTeamInBonus() {
        return homeTeamStats.getFouls() >= gameType.getBonusLimit();
    }

    public boolean isAwayTeamInBonus() {
        return awayTeamStats.getFouls() >= gameType.getBonusLimit();
    }

    public boolean isHomeTeamInDoubleBonus() {
        return gameType.getDoubleBonusLimit() > 0 &&
               homeTeamStats.getFouls() >= gameType.getDoubleBonusLimit();
    }

    public boolean isAwayTeamInDoubleBonus() {
        return gameType.getDoubleBonusLimit() > 0 &&
               awayTeamStats.getFouls() >= gameType.getDoubleBonusLimit();
    }

    public String getHomeBonusStatus() {
        if (isHomeTeamInDoubleBonus()) {
            return "DOUBLE BONUS";
        } else if (isHomeTeamInBonus()) {
            return "BONUS";
        }
        return "";
    }

    public String getAwayBonusStatus() {
        if (isAwayTeamInDoubleBonus()) {
            return "DOUBLE BONUS";
        } else if (isAwayTeamInBonus()) {
            return "BONUS";
        }
        return "";
    }

    // Getters and setters
    public String getGameId() { return gameId; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public TeamStats getHomeTeamStats() { return homeTeamStats; }
    public TeamStats getAwayTeamStats() { return awayTeamStats; }
    public int getCurrentPeriod() { return currentPeriod; }
    public String getStatus() { return status; }
    public List<String> getEventLog() { return eventLog; }
    public GameType getGameType() { return gameType; }
    public int getMaxPeriods() { return maxPeriods; }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
        this.maxPeriods = gameType.getPeriods();
    }
}