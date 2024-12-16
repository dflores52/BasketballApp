 //TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Initialize Game
        Game game = new Game("001", "Team A", "Team B");

        // Create Teams
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");

        // Add Players to Team A
        teamA.addPlayer(new Player("John Doe", 23));
        teamA.addPlayer(new Player("Jane Smith", 45));

        // Add Players to Team B
        teamB.addPlayer(new Player("Mike Johnson", 12));
        teamB.addPlayer(new Player("Emily Davis", 34));

        // Start the Game
        game.startGame();

        // Start Period 1
        game.startPeriod();

        // Simulate Scoring for Team A
        Player playerA1 = teamA.getPlayerByNumber(23); // John Doe
        if (playerA1 != null) {
            playerA1.addPoints(3);
            teamA.getTeamStats().addPoints(3);
            game.logEvent(playerA1.getPlayerName() + " scored a 3-pointer for " + teamA.getTeamName());
        }

        // Simulate a Foul for Team B
        Player playerB1 = teamB.getPlayerByNumber(12); // Mike Johnson
        if (playerB1 != null) {
            playerB1.addFoul();
            teamB.getTeamStats().addFoul();
            game.logEvent(playerB1.getPlayerName() + " committed a foul for " + teamB.getTeamName());
        }

        // Simulate Timeout for Team A
        teamA.getTeamStats().useTimeout();
        game.logEvent(teamA.getTeamName() + " used a timeout.");

        // End Period 1
        game.endPeriod();

        // Start Period 2
        game.startPeriod();

        // Simulate More Scoring
        if (playerA1 != null) {
            playerA1.addPoints(2);
            teamA.getTeamStats().addPoints(2);
            game.logEvent(playerA1.getPlayerName() + " scored a 2-pointer for " + teamA.getTeamName());
        }

        if (playerB1 != null) {
            playerB1.addPoints(2);
            teamB.getTeamStats().addPoints(2);
            game.logEvent(playerB1.getPlayerName() + " scored a 2-pointer for " + teamB.getTeamName());
        }

        // End the Game
        game.endGame();

        // Display Final Stats
        System.out.println("\n--- Final Game Stats ---");
        System.out.println("Game ID: " + game.getGameId());
        System.out.println("Home Team: " + teamA.getTeamName() + " | Score: " + teamA.getTeamStats().getScore());
        System.out.println("Away Team: " + teamB.getTeamName() + " | Score: " + teamB.getTeamStats().getScore());

        System.out.println("\nTeam A Players:");
        for (Player player : teamA.getPlayers()) {
            System.out.println(player.getPlayerName() + " - Points: " + player.getPoints() +
                    ", Fouls: " + player.getFouls());
        }

        System.out.println("\nTeam B Players:");
        for (Player player : teamB.getPlayers()) {
            System.out.println(player.getPlayerName() + " - Points: " + player.getPoints() +
                    ", Fouls: " + player.getFouls());
        }

        System.out.println("\nGame Log:");
        for (String event : game.getEventLog()) {
            System.out.println(event);
        }
    }


}