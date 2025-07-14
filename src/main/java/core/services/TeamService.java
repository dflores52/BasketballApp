package core.services;

import model.Team;
import model.Player;
import database.TeamDAO;
import core.exceptions.TeamException;
import java.sql.SQLException;
import java.util.List;

public class TeamService {
    private TeamDAO teamDAO;
    private static final int MAX_PLAYERS_PER_TEAM = 15;
    
    public TeamService() {
        this.teamDAO = new TeamDAO();
    }
    
    /**
     * Creates a new team with validation
     */
    public Team createTeam(String teamName, String coachName) throws TeamException, SQLException {
        // Validation
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new TeamException("Team name cannot be empty");
        }
        
        if (coachName == null || coachName.trim().isEmpty()) {
            throw new TeamException("Coach name cannot be empty");
        }
        
        if (isTeamNameTaken(teamName)) {
            throw new TeamException("Team name already exists: " + teamName);
        }
        
        // Create and save team
        teamDAO.addTeam(teamName, coachName);
        Team team = new Team(teamName);
        team.setCoachName(coachName);
        
        return team;
    }
    
    /**
     * Adds a player to a team with validation
     */
    public void addPlayerToTeam(Team team, Player player) throws TeamException {
        // Validation
        if (team.getPlayers().size() >= MAX_PLAYERS_PER_TEAM) {
            throw new TeamException("Team cannot have more than " + MAX_PLAYERS_PER_TEAM + " players");
        }
        
        if (isJerseyNumberTaken(team, player.getJerseyNumber())) {
            throw new TeamException("Jersey number " + player.getJerseyNumber() + " is already taken");
        }
        
        // Add player
        boolean added = team.addPlayer(player);
        if (!added) {
            throw new TeamException("Failed to add player to team");
        }
    }
    
    /**
     * Gets all teams from database
     */
    public List<Team> getAllTeams() throws SQLException {
        return teamDAO.getAllTeams();
    }
    
    /**
     * Updates an existing team
     */
    public void updateTeam(Team team) throws SQLException, TeamException {
        if (team == null) {
            throw new TeamException("Team cannot be null");
        }
        
        teamDAO.updateTeam(team);
    }
    
    /**
     * Deletes a team
     */
    public void deleteTeam(int teamId) throws SQLException {
        teamDAO.deleteTeam(teamId);
    }
    
    private boolean isTeamNameTaken(String teamName) throws SQLException {
        List<Team> teams = teamDAO.getAllTeams();
        return teams.stream()
            .anyMatch(team -> team.getTeamName().equalsIgnoreCase(teamName));
    }
    
    private boolean isJerseyNumberTaken(Team team, int jerseyNumber) {
        return team.getPlayers().stream()
            .anyMatch(player -> player.getJerseyNumber() == jerseyNumber);
    }
}
