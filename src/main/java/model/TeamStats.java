package model;

public class TeamStats {
    private String teamName;
    private int score;
    private int fouls;
    private int timeouts;
    private final int maxTimeouts = 7; // Standard basketball timeouts

    // Constructor
    public TeamStats(String teamName) {
        this.teamName = teamName;
        this.score = 0;
        this.fouls = 0;
        this.timeouts = maxTimeouts;
    }

    // Add points to the team's score
    public void addPoints(int points) {
        this.score += points;
        System.out.println(teamName + " scored " + points + " points. Total score: " + this.score);
    }

    // Add a foul to the team
    public void addFoul() {
        this.fouls++;
        System.out.println(teamName + " committed a foul. Total fouls: " + this.fouls);
    }

    // Reset fouls at the start of each period
    public void resetPeriodFouls() {
        this.fouls = 0;
        System.out.println(teamName + " fouls reset for new period");
    }

    // Use a timeout
    public boolean useTimeout() {
        if (timeouts > 0) {
            timeouts--;
            System.out.println(teamName + " used a timeout. Remaining timeouts: " + this.timeouts);
            return true;
        }
        return false;
    }

    // Getters
    public String getTeamName() {
        return teamName;
    }

    public int getScore() {
        return score;
    }

    public int getFouls() {
        return fouls;
    }

    public int getTimeouts() {
        return timeouts;
    }

    public int getMaxTimeouts() {
        return maxTimeouts;
    }
}