package model;

import java.time.LocalDateTime;

public class EventLog {
    // Attributes
    private String eventId;
    private LocalDateTime timestamp;
    private String eventType; // e.g., "Shot", "Foul", "Timeout", "Technical Foul"
    private String teamName;
    private String playerName;
    private String description; // Additional details (e.g., "3-pointer made", "model.Team timeout used")

    // Constructor
    public EventLog(String eventId, String eventType, String teamName, String playerName, String description) {
        this.eventId = eventId;
        this.timestamp = LocalDateTime.now(); // Captures the current time
        this.eventType = eventType;
        this.teamName = teamName;
        this.playerName = playerName;
        this.description = description;
    }

    // Getters
    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDescription() {
        return description;
    }

    // Convert to String for easy logging
    @Override
    public String toString() {
        return "[" + timestamp + "] " + "Event ID: " + eventId + ", Type: " + eventType +
                ", model.Team: " + teamName + ", model.Player: " + playerName + ", Details: " + description;
    }
}