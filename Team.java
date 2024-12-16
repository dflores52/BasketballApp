import java.util.ArrayList;
import java.util.List;
public class Team {
    // Attributes
    private String teamName;
    private List<Player> players;
    private TeamStats teamStats;

    // Constructor
    public Team(String teamName) {
        this.teamName = teamName;
        this.players = new ArrayList<>();
        this.teamStats = new TeamStats(teamName);
    }

    // Add a player to the team
    public void addPlayer(Player player) {
        players.add(player);
        System.out.println(player.getPlayerName() + " added to " + teamName);
    }

    // Get a player by their jersey number
    public Player getPlayerByNumber(int jerseyNumber) {
        for (Player player : players) {
            if (player.getJerseyNumber() == jerseyNumber) {
                return player;
            }
        }
        System.out.println("Player with jersey number " + jerseyNumber + " not found.");
        return null;
    }

    // Getters and Setters
    public String getTeamName() {
        return teamName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public TeamStats getTeamStats() {
        return teamStats;
    }
}
