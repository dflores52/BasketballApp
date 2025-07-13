package model;

import java.util.ArrayList;

public class Game {


    private final TeamStats homeTeamStats;
    private final TeamStats awayTeamStats;
    private final ArrayList<String> eventLog; // Specify generic type as String
    private final String gameId;
    private String homeTeam;
    private String awayTeam;
    private String status;
    private int currentPeriod;


    public Game(String gameId, String homeTeam, String awayTeam){
        this.gameId = gameId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.status = "Not Started";
        this.currentPeriod = 0;
        this.eventLog = new ArrayList<>();
        this.homeTeamStats = new TeamStats(homeTeam);
        this.awayTeamStats = new TeamStats(awayTeam);

    }
    public void startGame(){
        this.status = "Ongoing";
        this.currentPeriod = 1;
        logEvent("model.Game started: " + homeTeam + " vs. " + awayTeam);
    }

    public void startPeriod(){
        currentPeriod ++;
    }

    public void endGame(){
        this.status = "Completed";
        logEvent("model.Game ended. Final score - " + homeTeam + ": "
                + homeTeamStats.getScore() + ", " + awayTeam + ": "
                + awayTeamStats.getScore());

    }
    // Add an event to the log
    public void logEvent(String event) {
        this.eventLog.add(event);
        System.out.println(event);
    }

    // Getters
    public String getGameId() {
        return gameId;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getStatus() {
        return status;
    }

    public int getCurrentPeriod() {
        return currentPeriod;
    }

    public TeamStats getHomeTeamStats() {
        return homeTeamStats;
    }

    public TeamStats getAwayTeamStats() {
        return awayTeamStats;
    }

    public ArrayList<String> getEventLog() {
        return eventLog;
    }

    public void endPeriod() {
    }
}

