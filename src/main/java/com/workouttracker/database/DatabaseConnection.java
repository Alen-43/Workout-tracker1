package com.workouttracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DEFAULT_DB_FILE = "workout_tracker.db";
    private static String dbUrl = "jdbc:sqlite:" + DEFAULT_DB_FILE;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found! Ensure sqlite-jdbc dependency is present.");
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {
    }

    public static void setDbUrl(String customDbUrl) {
        dbUrl = customDbUrl;
    }

    public static void resetToDefaultDbUrl() {
        dbUrl = "jdbc:sqlite:" + DEFAULT_DB_FILE;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS workouts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "body_section TEXT NOT NULL DEFAULT 'Chest', "
                + "exercise_name TEXT NOT NULL, "
                + "workout_date TEXT NOT NULL, "
                + "weight_kg REAL DEFAULT 0, "
                + "reps INTEGER DEFAULT 10, "
                + "duration_minutes INTEGER DEFAULT 5, "
                + "calories_burned INTEGER DEFAULT 25"
                + ");";

        String createGoalsTableSQL = "CREATE TABLE IF NOT EXISTS fitness_goals ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "goal_type TEXT NOT NULL DEFAULT 'Hypertrophy', "
                + "target_frequency INTEGER NOT NULL DEFAULT 4, "
                + "target_duration_mins INTEGER NOT NULL DEFAULT 180, "
                + "target_calories INTEGER NOT NULL DEFAULT 1500, "
                + "created_at TEXT NOT NULL"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSQL);
            stmt.execute(createGoalsTableSQL);

            try {
                stmt.execute("ALTER TABLE workouts ADD COLUMN body_section TEXT DEFAULT 'Chest';");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE workouts ADD COLUMN reps INTEGER DEFAULT 10;");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE workouts ADD COLUMN weight_kg REAL DEFAULT 0;");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE workouts ADD COLUMN duration_minutes INTEGER DEFAULT 5;");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE workouts ADD COLUMN calories_burned INTEGER DEFAULT 25;");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE workouts DROP COLUMN notes;");
            } catch (SQLException ignored) {}

            System.out.println("[DatabaseConnection] SQLite database and tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
