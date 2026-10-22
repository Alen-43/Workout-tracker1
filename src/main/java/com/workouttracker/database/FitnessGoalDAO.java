package com.workouttracker.database;

import com.workouttracker.model.FitnessGoal;
import com.workouttracker.model.WeeklyProgressReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

public class FitnessGoalDAO {

    public FitnessGoal getGoal() throws SQLException {
        String sql = "SELECT id, goal_type, target_frequency, target_duration_mins, target_calories, created_at "
                   + "FROM fitness_goals ORDER BY id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return new FitnessGoal(
                        rs.getInt("id"),
                        rs.getString("goal_type"),
                        rs.getInt("target_frequency"),
                        rs.getInt("target_duration_mins"),
                        rs.getInt("target_calories"),
                        rs.getString("created_at")
                );
            }
        }
        return null;
    }

    public boolean saveOrUpdateGoal(FitnessGoal goal) throws SQLException {
        FitnessGoal existing = getGoal();
        if (existing == null) {
            String insertSql = "INSERT INTO fitness_goals (goal_type, target_frequency, target_duration_mins, target_calories, created_at) "
                             + "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, goal.getGoalType());
                pstmt.setInt(2, goal.getTargetFrequency());
                pstmt.setInt(3, goal.getTargetDurationMins());
                pstmt.setInt(4, goal.getTargetCalories());
                pstmt.setString(5, goal.getCreatedAt());

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            goal.setId(keys.getInt(1));
                        }
                    }
                    return true;
                }
                return false;
            }
        } else {
            String updateSql = "UPDATE fitness_goals SET goal_type = ?, target_frequency = ?, "
                             + "target_duration_mins = ?, target_calories = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

                pstmt.setString(1, goal.getGoalType());
                pstmt.setInt(2, goal.getTargetFrequency());
                pstmt.setInt(3, goal.getTargetDurationMins());
                pstmt.setInt(4, goal.getTargetCalories());
                pstmt.setInt(5, existing.getId());

                goal.setId(existing.getId());
                return pstmt.executeUpdate() > 0;
            }
        }
    }

    public WeeklyProgressReport getWeeklyProgressReport(LocalDate date) throws SQLException {
        if (date == null) date = LocalDate.now();

        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        FitnessGoal goal = getGoal();
        if (goal == null) {
            goal = new FitnessGoal(FitnessGoal.HYPERTROPHY, 4, 180, 1500);
        }

        String sql = "SELECT workout_date, duration_minutes, calories_burned FROM workouts "
                   + "WHERE workout_date >= ? AND workout_date <= ?";

        Set<String> distinctDates = new HashSet<>();
        int totalDuration = 0;
        int totalCalories = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, weekStart.toString());
            pstmt.setString(2, weekEnd.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    distinctDates.add(rs.getString("workout_date"));

                    int duration = rs.getInt("duration_minutes");
                    if (duration <= 0) duration = 5;
                    totalDuration += duration;

                    int calories = rs.getInt("calories_burned");
                    if (calories <= 0) calories = 25;
                    totalCalories += calories;
                }
            }
        }

        int actualFrequency = distinctDates.size();
        return new WeeklyProgressReport(goal, weekStart, weekEnd, actualFrequency, totalDuration, totalCalories);
    }
}
