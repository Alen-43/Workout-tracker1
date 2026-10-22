package com.workouttracker.database;

import com.workouttracker.model.Workout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkoutDAO {

    public boolean addWorkout(Workout workout) throws SQLException {
        String sql = "INSERT INTO workouts (body_section, exercise_name, workout_date, weight_kg, reps, duration_minutes, calories_burned) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, workout.getBodySection());
            pstmt.setString(2, workout.getExerciseName());
            pstmt.setString(3, workout.getDate());
            pstmt.setDouble(4, workout.getWeightKg());
            pstmt.setInt(5, workout.getReps());
            pstmt.setInt(6, workout.getDurationMinutes());
            pstmt.setInt(7, workout.getCaloriesBurned());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        workout.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public List<Workout> getAllWorkouts() throws SQLException {
        List<Workout> workouts = new ArrayList<>();
        String sql = "SELECT id, body_section, exercise_name, workout_date, weight_kg, reps, duration_minutes, calories_burned "
                   + "FROM workouts ORDER BY workout_date DESC, id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                workouts.add(mapRowToWorkout(rs));
            }
        }
        return workouts;
    }

    public List<Workout> getWorkoutsByDate(String date) throws SQLException {
        List<Workout> workouts = new ArrayList<>();
        String sql = "SELECT id, body_section, exercise_name, workout_date, weight_kg, reps, duration_minutes, calories_burned "
                   + "FROM workouts WHERE workout_date = ? ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapRowToWorkout(rs));
                }
            }
        }
        return workouts;
    }

    public List<Workout> getWorkoutsByMonth(int year, int month) throws SQLException {
        List<Workout> workouts = new ArrayList<>();
        String monthPattern = String.format("%04d-%02d%%", year, month);
        String sql = "SELECT id, body_section, exercise_name, workout_date, weight_kg, reps, duration_minutes, calories_burned "
                   + "FROM workouts WHERE workout_date LIKE ? ORDER BY workout_date DESC, id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, monthPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapRowToWorkout(rs));
                }
            }
        }
        return workouts;
    }

    public List<Workout> getWorkoutsBetweenDates(String startDate, String endDate) throws SQLException {
        List<Workout> workouts = new ArrayList<>();
        String sql = "SELECT id, body_section, exercise_name, workout_date, weight_kg, reps, duration_minutes, calories_burned "
                   + "FROM workouts WHERE workout_date >= ? AND workout_date <= ? ORDER BY workout_date ASC, id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate.trim());
            pstmt.setString(2, endDate.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapRowToWorkout(rs));
                }
            }
        }
        return workouts;
    }

    public Set<String> getDistinctWorkoutDatesInMonth(int year, int month) throws SQLException {
        Set<String> dates = new HashSet<>();
        String monthPattern = String.format("%04d-%02d%%", year, month);
        String sql = "SELECT DISTINCT workout_date FROM workouts WHERE workout_date LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, monthPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getString("workout_date"));
                }
            }
        }
        return dates;
    }

    public Workout getWorkoutById(int id) throws SQLException {
        String sql = "SELECT id, body_section, exercise_name, workout_date, weight_kg, reps, duration_minutes, calories_burned "
                   + "FROM workouts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWorkout(rs);
                }
            }
        }
        return null;
    }

    public boolean updateWorkout(Workout workout) throws SQLException {
        String sql = "UPDATE workouts SET body_section = ?, exercise_name = ?, workout_date = ?, "
                   + "weight_kg = ?, reps = ?, duration_minutes = ?, calories_burned = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, workout.getBodySection());
            pstmt.setString(2, workout.getExerciseName());
            pstmt.setString(3, workout.getDate());
            pstmt.setDouble(4, workout.getWeightKg());
            pstmt.setInt(5, workout.getReps());
            pstmt.setInt(6, workout.getDurationMinutes());
            pstmt.setInt(7, workout.getCaloriesBurned());
            pstmt.setInt(8, workout.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public boolean deleteWorkout(int id) throws SQLException {
        String sql = "DELETE FROM workouts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    private Workout mapRowToWorkout(ResultSet rs) throws SQLException {
        String bodySection = "General";
        try {
            bodySection = rs.getString("body_section");
            if (bodySection == null) bodySection = "General";
        } catch (SQLException ignored) {}

        int reps = 10;
        try {
            reps = rs.getInt("reps");
            if (reps <= 0) reps = 10;
        } catch (SQLException ignored) {}

        int duration = 5;
        try {
            duration = rs.getInt("duration_minutes");
            if (duration <= 0) duration = 5;
        } catch (SQLException ignored) {}

        int calories = 25;
        try {
            calories = rs.getInt("calories_burned");
            if (calories <= 0) calories = 25;
        } catch (SQLException ignored) {}

        return new Workout(
                rs.getInt("id"),
                bodySection,
                rs.getString("exercise_name"),
                rs.getString("workout_date"),
                rs.getDouble("weight_kg"),
                reps,
                duration,
                calories
        );
    }
}
