package util;

public class SessionManager {
    private static SessionManager instance;
    private String currentUsername;
    private String userRole;
    private boolean isLoggedIn;

    private SessionManager() {
        // Private constructor for singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(String username, String role) {
        this.currentUsername = username;
        this.userRole = role;
        this.isLoggedIn = true;
    }

    public void endSession() {
        this.currentUsername = null;
        this.userRole = null;
        this.isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean hasPermission(String requiredRole) {
        if (userRole == null) return false;
        
        // Admin has all permissions
        if (userRole.equals("ADMIN")) return true;
        
        // Otherwise, check if user has the specific role
        return userRole.equals(requiredRole);
    }
}
