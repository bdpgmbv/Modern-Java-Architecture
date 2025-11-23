package com.example.config.factory;

/**
 * Example class representing a complex object that requires
 * special creation logic.
 *
 * This could be a database connection, HTTP client, or any
 * object that needs complex initialization.
 */
public class DatabaseConnection {

    private final String url;
    private final String username;
    private final int maxConnections;
    private final boolean poolingEnabled;

    public DatabaseConnection(String url, String username, int maxConnections, boolean poolingEnabled) {
        this.url = url;
        this.username = username;
        this.maxConnections = maxConnections;
        this.poolingEnabled = poolingEnabled;

        System.out.println("DatabaseConnection created:");
        System.out.println("  URL: " + url);
        System.out.println("  Username: " + username);
        System.out.println("  Max Connections: " + maxConnections);
        System.out.println("  Pooling: " + poolingEnabled);
    }

    public String getConnectionInfo() {
        return String.format("Connected to %s as %s (max: %d, pooling: %s)",
                url, username, maxConnections, poolingEnabled);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public boolean isPoolingEnabled() {
        return poolingEnabled;
    }
}
