import java.util.ArrayList;
import java.util.List;

public class EventLogManager {
    // List to store all events
    private List<EventLog> eventLogs;

    // Constructor
    public EventLogManager() {
        this.eventLogs = new ArrayList<>();
    }

    // Add a new event to the log
    public void addEvent(String eventId, String eventType, String teamName, String playerName, String description) {
        EventLog newEvent = new EventLog(eventId, eventType, teamName, playerName, description);
        eventLogs.add(newEvent);
        System.out.println("Event Logged: " + newEvent.toString());
    }

    // Retrieve all events
    public List<EventLog> getAllEvents() {
        return eventLogs;
    }

    // Display all events
    public void displayEventLog() {
        System.out.println("\n--- Game Event Log ---");
        for (EventLog event : eventLogs) {
            System.out.println(event.toString());
        }
    }
}
