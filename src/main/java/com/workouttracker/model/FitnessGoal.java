package com.workouttracker.model;

import java.time.LocalDate;

public class FitnessGoal {

    public static final String HYPERTROPHY = "Hypertrophy";
    public static final String STRENGTH_TRAINING = "Strength Training";
    public static final String WEIGHT_LOSS = "Weight Loss";

    private int id;
    private String goalType;
    private int targetFrequency;
    private int targetDurationMins;
    private int targetCalories;
    private String createdAt;

    public FitnessGoal() {
        this(0, HYPERTROPHY, 4, 180, 1500, LocalDate.now().toString());
    }

    public FitnessGoal(String goalType, int targetFrequency, int targetDurationMins, int targetCalories) {
        this(0, goalType, targetFrequency, targetDurationMins, targetCalories, LocalDate.now().toString());
    }

    public FitnessGoal(int id, String goalType, int targetFrequency, int targetDurationMins, int targetCalories, String createdAt) {
        this.id = id;
        setGoalType(goalType);
        setTargetFrequency(targetFrequency);
        setTargetDurationMins(targetDurationMins);
        setTargetCalories(targetCalories);
        this.createdAt = (createdAt != null && !createdAt.trim().isEmpty()) ? createdAt.trim() : LocalDate.now().toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        if (goalType == null || goalType.trim().isEmpty()) {
            throw new IllegalArgumentException("Goal type cannot be empty.");
        }
        String trimmed = goalType.trim();
        if (!trimmed.equalsIgnoreCase(HYPERTROPHY) &&
            !trimmed.equalsIgnoreCase(STRENGTH_TRAINING) &&
            !trimmed.equalsIgnoreCase(WEIGHT_LOSS)) {
            throw new IllegalArgumentException("Goal type must be Hypertrophy, Strength Training, or Weight Loss.");
        }
        this.goalType = trimmed;
    }

    public int getTargetFrequency() {
        return targetFrequency;
    }

    public void setTargetFrequency(int targetFrequency) {
        if (targetFrequency < 1 || targetFrequency > 7) {
            throw new IllegalArgumentException("Target frequency must be between 1 and 7 days per week.");
        }
        this.targetFrequency = targetFrequency;
    }

    public int getTargetDurationMins() {
        return targetDurationMins;
    }

    public void setTargetDurationMins(int targetDurationMins) {
        if (targetDurationMins <= 0) {
            throw new IllegalArgumentException("Target duration must be greater than 0 minutes.");
        }
        this.targetDurationMins = targetDurationMins;
    }

    public int getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(int targetCalories) {
        if (targetCalories <= 0) {
            throw new IllegalArgumentException("Target calories must be greater than 0.");
        }
        this.targetCalories = targetCalories;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FitnessGoal{" +
                "id=" + id +
                ", goalType='" + goalType + '\'' +
                ", targetFrequency=" + targetFrequency +
                ", targetDurationMins=" + targetDurationMins +
                ", targetCalories=" + targetCalories +
                '}';
    }
}
