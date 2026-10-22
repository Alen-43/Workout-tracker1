package com.workouttracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeeklyProgressReport {

    private final FitnessGoal goal;
    private final LocalDate weekStart;
    private final LocalDate weekEnd;
    private final int actualFrequency;
    private final int actualDurationMins;
    private final int actualCalories;

    public WeeklyProgressReport(FitnessGoal goal, LocalDate weekStart, LocalDate weekEnd,
                                int actualFrequency, int actualDurationMins, int actualCalories) {
        this.goal = (goal != null) ? goal : new FitnessGoal();
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.actualFrequency = Math.max(0, actualFrequency);
        this.actualDurationMins = Math.max(0, actualDurationMins);
        this.actualCalories = Math.max(0, actualCalories);
    }

    public FitnessGoal getGoal() {
        return goal;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public int getActualFrequency() {
        return actualFrequency;
    }

    public int getActualDurationMins() {
        return actualDurationMins;
    }

    public int getActualCalories() {
        return actualCalories;
    }

    public int getTargetFrequency() {
        return goal.getTargetFrequency();
    }

    public int getTargetDurationMins() {
        return goal.getTargetDurationMins();
    }

    public int getTargetCalories() {
        return goal.getTargetCalories();
    }

    public boolean isFrequencyCompleted() {
        return actualFrequency >= getTargetFrequency();
    }

    public boolean isDurationCompleted() {
        return actualDurationMins >= getTargetDurationMins();
    }

    public boolean isCaloriesCompleted() {
        return actualCalories >= getTargetCalories();
    }

    public int getFrequencyPercent() {
        if (getTargetFrequency() <= 0) return 0;
        return (int) Math.min(100, Math.round((actualFrequency * 100.0) / getTargetFrequency()));
    }

    public int getDurationPercent() {
        if (getTargetDurationMins() <= 0) return 0;
        return (int) Math.min(100, Math.round((actualDurationMins * 100.0) / getTargetDurationMins()));
    }

    public int getCaloriesPercent() {
        if (getTargetCalories() <= 0) return 0;
        return (int) Math.min(100, Math.round((actualCalories * 100.0) / getTargetCalories()));
    }

    public List<String> getCompletedHighlights() {
        List<String> list = new ArrayList<>();
        if (isFrequencyCompleted()) {
            list.add("Workout Frequency: Completed " + actualFrequency + " of " + getTargetFrequency() + " target days (" + getFrequencyPercent() + "%).");
        }
        if (isDurationCompleted()) {
            list.add("Workout Duration: Logged " + actualDurationMins + " of " + getTargetDurationMins() + " target active minutes (" + getDurationPercent() + "%).");
        }
        if (isCaloriesCompleted()) {
            list.add("Calories Burned: Burned " + actualCalories + " of " + getTargetCalories() + " target calories (" + getCaloriesPercent() + "%).");
        }
        if (list.isEmpty()) {
            list.add("No weekly targets completed yet. Stay consistent with your training schedule!");
        }
        return list;
    }

    public List<String> getAreasNeedingImprovement() {
        List<String> list = new ArrayList<>();
        if (!isFrequencyCompleted()) {
            int diff = getTargetFrequency() - actualFrequency;
            list.add("Workout Frequency: " + diff + " more workout day" + (diff > 1 ? "s" : "") + " needed this week (Currently: " + actualFrequency + "/" + getTargetFrequency() + " days).");
        }
        if (!isDurationCompleted()) {
            int diff = getTargetDurationMins() - actualDurationMins;
            list.add("Workout Duration: " + diff + " active minutes behind target (Currently: " + actualDurationMins + "/" + getTargetDurationMins() + " mins). Consider extending your sessions.");
        }
        if (!isCaloriesCompleted()) {
            int diff = getTargetCalories() - actualCalories;
            list.add("Calories Burned: " + diff + " kcal remaining to hit your target (Currently: " + actualCalories + "/" + getTargetCalories() + " kcal). Try increasing set volume or intensity.");
        }
        if (list.isEmpty()) {
            list.add("All targets completed! Outstanding discipline and performance this week.");
        }
        return list;
    }
}
