public class Player {



        private String playerName;
        private int jerseyNumber;
        private int points;
        private int fouls;
        private int assists;
        private int rebounds;

        // Constructor
        public Player(String playerName, int jerseyNumber) {
            this.playerName = playerName;
            this.jerseyNumber = jerseyNumber;
            this.points = 0;
            this.fouls = 0;
            this.assists = 0;
            this.rebounds = 0;
        }

        // Methods

        // Add points scored by the player
        public void addPoints(int points) {
            this.points += points;
            System.out.println(playerName + " scored " + points + " points. Total points: " + this.points);
        }

        // Add a foul committed by the player
        public void addFoul() {
            this.fouls++;
            System.out.println(playerName + " committed a foul. Total fouls: " + this.fouls);
        }

        // Add an assist by the player
        public void addAssist() {
            this.assists++;
            System.out.println(playerName + " made an assist. Total assists: " + this.assists);
        }

        // Add a rebound by the player
        public void addRebound() {
            this.rebounds++;
            System.out.println(playerName + " grabbed a rebound. Total rebounds: " + this.rebounds);
        }

        // Getters and Setters
        public String getPlayerName() {
            return playerName;
        }

        public int getJerseyNumber() {
            return jerseyNumber;
        }

        public int getPoints() {
            return points;
        }

        public int getFouls() {
            return fouls;
        }

        public int getAssists() {
            return assists;
        }

        public int getRebounds() {
            return rebounds;
        }
    }


}
