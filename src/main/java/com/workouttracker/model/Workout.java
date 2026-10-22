package com.workouttracker.model;

public class Workout {

    private int id;
    private String bodySection;
    private String exerciseName;
    private String date;
    private double weightKg;
    private int reps;
    private int durationMinutes;
    private int caloriesBurned;

    public Workout() {
        this.bodySection = "General";
        this.exerciseName = "";
        this.date = "";
        this.weightKg = 0.0;
        this.reps = 10;
        this.durationMinutes = 5;
        this.caloriesBurned = 25;
    }

    public Workout(String bodySection, String exerciseName, String date, double weightKg, int reps) {
        this(0, bodySection, exerciseName, date, weightKg, reps, 5, 25);
    }

    public Workout(String bodySection, String exerciseName, String date, double weightKg, int reps, int durationMinutes, int caloriesBurned) {
        this(0, bodySection, exerciseName, date, weightKg, reps, durationMinutes, caloriesBurned);
    }

    public Workout(int id, String bodySection, String exerciseName, String date, double weightKg, int reps) {
        this(id, bodySection, exerciseName, date, weightKg, reps, 5, 25);
    }

    public Workout(int id, String bodySection, String exerciseName, String date, double weightKg, int reps, int durationMinutes, int caloriesBurned) {
        this.id = id;
        setBodySection(bodySection);
        setExerciseName(exerciseName);
        setDate(date);
        setWeightKg(weightKg);
        setReps(reps);
        setDurationMinutes(durationMinutes);
        setCaloriesBurned(caloriesBurned);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBodySection() {
        return bodySection != null ? bodySection : "General";
    }

    public void setBodySection(String bodySection) {
        this.bodySection = (bodySection != null && !bodySection.trim().isEmpty()) ? bodySection.trim() : "General";
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        if (exerciseName == null || exerciseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Exercise name cannot be empty.");
        }
        this.exerciseName = exerciseName.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }
        String trimmed = date.trim();
        if (!trimmed.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format (e.g., 2026-10-22).");
        }
        this.date = trimmed;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (weightKg < 0) {
            throw new IllegalArgumentException("Weight cannot be a negative number.");
        }
        this.weightKg = weightKg;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        if (reps <= 0) {
            throw new IllegalArgumentException("Reps must be a positive whole number (at least 1).");
        }
        this.reps = reps;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative.");
        }
        this.durationMinutes = durationMinutes;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        if (caloriesBurned < 0) {
            throw new IllegalArgumentException("Calories burned cannot be negative.");
        }
        this.caloriesBurned = caloriesBurned;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", bodySection='" + bodySection + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                ", date='" + date + '\'' +
                ", weightKg=" + weightKg +
                ", reps=" + reps +
                ", durationMinutes=" + durationMinutes +
                ", caloriesBurned=" + caloriesBurned +
                '}';
    }
}
