package model;

public class TeamStats {
    // Attributes
    private String teamName;
    private int score;
    private int fouls;
    private int technicalFouls;
    private int timeoutsUsed;
    private int timeoutLimit; // Configurable based on league rules
    private boolean inBonus;

    // Constructor
    public TeamStats(String teamName) {
        this.teamName = teamName;
        this.score = 0;
        this.fouls = 0;
        this.technicalFouls = 0;
        this.timeoutsUsed = 0;
        this.timeoutLimit = 5; // Default limit, can be adjusted
        this.inBonus = false;
    }


        // Methods

        // Add points to the team's score
        public void addPoints(int points) {
            this.score += points;
            System.out.println(teamName + " scored " + points + " points. Total score: " + this.score);
        }

        // Record a foul
        public void addFoul() {
            this.fouls++;
            System.out.println(teamName + " committed a foul. Total fouls: " + this.fouls);
            checkBonus();
        }

        // Record a technical foul
        public void addTechnicalFoul() {
            this.technicalFouls++;
            System.out.println(teamName + " committed a technical foul. Total technical fouls: " + this.technicalFouls);
        }

        // Use a timeout
        public void useTimeout() {
            if (timeoutsUsed < timeoutLimit) {
                this.timeoutsUsed++;
                System.out.println(teamName + " used a timeout. Timeouts used: " + this.timeoutsUsed);
            } else {
                System.out.println(teamName + " has no timeouts remaining.");
            }
        }

        // Check if the team has entered the bonus
        private void checkBonus() {
            if (this.fouls >= 7 && !this.inBonus) { // Example bonus rule
                this.inBonus = true;
                System.out.println(teamName + " is now in the bonus.");
            }
        }

        // Reset stats for a new period
        public void resetPeriodStats() {
            this.fouls = 0;
            this.inBonus = false;
            System.out.println("Period stats reset for " + teamName);
        }

        // Getters and Setters
        public String getTeamName() {
            return teamName;
        }

        public int getScore() {
            return score;
        }

        public int getFouls() {
            return fouls;
        }

        public int getTechnicalFouls() {
            return technicalFouls;
        }

        public int getTimeoutsUsed() {
            return timeoutsUsed;
        }

        public int getTimeoutLimit() {
            return timeoutLimit;
        }

        public void setTimeoutLimit(int timeoutLimit) {
            this.timeoutLimit = timeoutLimit;
        }

        public boolean isInBonus() {
            return inBonus;
        }
    }

