package com.workouttracker;

import com.workouttracker.database.DatabaseConnection;
import com.workouttracker.database.FitnessGoalDAO;
import com.workouttracker.database.WorkoutDAO;
import com.workouttracker.model.DietPlan;
import com.workouttracker.model.FitnessGoal;
import com.workouttracker.model.WeeklyProgressReport;
import com.workouttracker.model.Workout;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkoutTrackerTest {

    private static final String TEST_DB_FILE = "test_workouts_v3.db";
    private static WorkoutDAO dao;
    private static FitnessGoalDAO goalDao;
    private static int createdWorkoutId;

    @BeforeAll
    public static void setUpClass() {
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.setDbUrl("jdbc:sqlite:" + TEST_DB_FILE);
        DatabaseConnection.initializeDatabase();
        dao = new WorkoutDAO();
        goalDao = new FitnessGoalDAO();
    }

    @AfterAll
    public static void tearDownClass() {
        DatabaseConnection.resetToDefaultDbUrl();
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Model Validation: Rejects Empty Exercise Name")
    public void testModelRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Workout("Chest", "", "2026-10-22", 60.0, 10);
        });
    }

    @Test
    @Order(2)
    @DisplayName("Model Validation: Rejects Invalid Date Format")
    public void testModelRejectsInvalidDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Workout("Chest", "Bench Press", "22/10/2026", 60.0, 10);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Model Validation: Rejects Negative Weight")
    public void testModelRejectsNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Workout("Chest", "Bench Press", "2026-10-22", -5.0, 10);
        });
    }

    @Test
    @Order(4)
    @DisplayName("Model Validation: Rejects Non-Positive Reps")
    public void testModelRejectsNonPositiveReps() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Workout("Chest", "Bench Press", "2026-10-22", 60.0, 0);
        });
    }

    @Test
    @Order(5)
    @DisplayName("CRUD - Create: Insert Set with Body Section and Reps")
    public void testAddWorkoutSet() throws SQLException {
        Workout set1 = new Workout("Chest", "Bench Press", "2026-10-20", 60.0, 10, 8, 45);
        boolean success = dao.addWorkout(set1);

        assertTrue(success, "Workout set insertion should succeed.");
        assertTrue(set1.getId() > 0, "Generated ID should be greater than 0.");
        assertEquals(8, set1.getDurationMinutes());
        assertEquals(45, set1.getCaloriesBurned());
        createdWorkoutId = set1.getId();
    }

    @Test
    @Order(6)
    @DisplayName("CRUD - Multi-Set: Repeated Sets for Same Exercise on Same Day")
    public void testRepeatedSetsAllowed() throws SQLException {
        Workout set2 = new Workout("Chest", "Bench Press", "2026-10-20", 62.5, 8, 6, 35);
        Workout set3 = new Workout("Chest", "Bench Press", "2026-10-20", 65.0, 6, 5, 30);

        assertTrue(dao.addWorkout(set2));
        assertTrue(dao.addWorkout(set3));

        List<Workout> setsOnDate = dao.getWorkoutsByDate("2026-10-20");
        assertEquals(3, setsOnDate.size(), "Should contain all 3 sets for 2026-10-20.");
        assertEquals("Chest", setsOnDate.get(0).getBodySection());
        assertEquals("Bench Press", setsOnDate.get(0).getExerciseName());
    }

    @Test
    @Order(7)
    @DisplayName("Calendar Queries: Distinct Workout Dates in Month for Green Marking")
    public void testDistinctWorkoutDatesForCalendar() throws SQLException {
        Workout legsSet = new Workout("Legs", "Squat", "2026-10-22", 80.0, 5, 10, 50);
        dao.addWorkout(legsSet);

        Set<String> activeDates = dao.getDistinctWorkoutDatesInMonth(2026, 10);
        assertTrue(activeDates.contains("2026-10-20"));
        assertTrue(activeDates.contains("2026-10-22"));
        assertFalse(activeDates.contains("2026-10-21"), "Date without workouts should not be active.");
    }

    @Test
    @Order(8)
    @DisplayName("CRUD - Delete: Remove Specific Workout Set")
    public void testDeleteWorkout() throws SQLException {
        boolean deleted = dao.deleteWorkout(createdWorkoutId);
        assertTrue(deleted, "Deletion should return true.");

        Workout fetched = dao.getWorkoutById(createdWorkoutId);
        assertNull(fetched, "Set should no longer exist after deletion.");
    }

    @Test
    @Order(9)
    @DisplayName("UI Button Styling: High Contrast & BasicButtonUI configured")
    public void testUIButtonStyling() {
        javax.swing.JButton primaryBtn = com.workouttracker.ui.UIUtils.createPrimaryButton("Add Workout");
        assertTrue(primaryBtn.isOpaque(), "Primary button must be opaque for custom background");
        assertTrue(primaryBtn.isContentAreaFilled(), "Primary button content area must be filled");
        assertEquals(java.awt.Color.WHITE, primaryBtn.getForeground(), "Primary button text must be white");
        assertEquals(new java.awt.Color(37, 99, 235), primaryBtn.getBackground(), "Primary button background must be blue");
        assertTrue(primaryBtn.getUI() instanceof javax.swing.plaf.basic.BasicButtonUI, "Must use BasicButtonUI to avoid Windows LookAndFeel overriding background");

        javax.swing.JButton secondaryBtn = com.workouttracker.ui.UIUtils.createSecondaryButton("Cancel");
        assertTrue(secondaryBtn.isOpaque());
        assertEquals(new java.awt.Color(30, 41, 59), secondaryBtn.getForeground(), "Secondary button text must be dark slate");

        javax.swing.JButton dangerBtn = com.workouttracker.ui.UIUtils.createDangerButton("Delete");
        assertTrue(dangerBtn.isOpaque());
        assertEquals(new java.awt.Color(185, 28, 28), dangerBtn.getForeground(), "Danger button text must be crimson");

        javax.swing.JButton navBtn = com.workouttracker.ui.UIUtils.createNavButton(" < ");
        assertTrue(navBtn.isOpaque());
        assertEquals(new java.awt.Color(30, 41, 59), navBtn.getForeground(), "Nav button text must be dark");
    }

    @Test
    @Order(10)
    @DisplayName("Migration: Safely Handles Legacy duration_minutes Column with NOT NULL constraint")
    public void testMigrationLegacyDuration() throws SQLException {
        String legacyDb = "legacy_test.db";
        File f = new File(legacyDb);
        if (f.exists()) f.delete();

        try {
            DatabaseConnection.setDbUrl("jdbc:sqlite:" + legacyDb);
            try (java.sql.Connection conn = DatabaseConnection.getConnection();
                 java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE workouts ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "exercise_name TEXT NOT NULL, "
                        + "workout_date TEXT NOT NULL, "
                        + "duration_minutes INTEGER NOT NULL, "
                        + "notes TEXT NOT NULL);");
                stmt.execute("INSERT INTO workouts (exercise_name, workout_date, duration_minutes, notes) VALUES ('Squat', '2026-10-20', 45, 'heavy');");
            }

            DatabaseConnection.initializeDatabase();

            WorkoutDAO legacyDao = new WorkoutDAO();
            Workout newSet = new Workout("Chest", "Bench Press", "2026-10-22", 60.0, 10);
            boolean added = legacyDao.addWorkout(newSet);
            assertTrue(added, "Should successfully insert new set into migrated database");

            List<Workout> all = legacyDao.getAllWorkouts();
            assertEquals(2, all.size(), "Both old and new records should be present");
        } finally {
            DatabaseConnection.setDbUrl("jdbc:sqlite:" + TEST_DB_FILE);
            if (f.exists()) f.delete();
        }
    }

    @Test
    @Order(11)
    @DisplayName("FitnessGoal: Validates Goal Types and Target Thresholds")
    public void testFitnessGoalValidation() {
        FitnessGoal goal = new FitnessGoal(FitnessGoal.HYPERTROPHY, 4, 180, 1500);
        assertEquals(FitnessGoal.HYPERTROPHY, goal.getGoalType());
        assertEquals(4, goal.getTargetFrequency());
        assertEquals(180, goal.getTargetDurationMins());
        assertEquals(1500, goal.getTargetCalories());

        assertThrows(IllegalArgumentException.class, () -> new FitnessGoal("YogaOnly", 4, 180, 1500));
        assertThrows(IllegalArgumentException.class, () -> new FitnessGoal(FitnessGoal.WEIGHT_LOSS, 0, 180, 1500));
        assertThrows(IllegalArgumentException.class, () -> new FitnessGoal(FitnessGoal.WEIGHT_LOSS, 8, 180, 1500));
        assertThrows(IllegalArgumentException.class, () -> new FitnessGoal(FitnessGoal.WEIGHT_LOSS, 4, 0, 1500));
        assertThrows(IllegalArgumentException.class, () -> new FitnessGoal(FitnessGoal.WEIGHT_LOSS, 4, 180, 0));
    }

    @Test
    @Order(12)
    @DisplayName("DietPlan: Provides Tailored Protocols for Hypertrophy, Strength, and Weight Loss")
    public void testDietPlanGeneration() {
        DietPlan hypertrophyDiet = DietPlan.forGoal(FitnessGoal.HYPERTROPHY);
        assertNotNull(hypertrophyDiet);
        assertTrue(hypertrophyDiet.getTitle().contains("Hypertrophy"));
        assertTrue(hypertrophyDiet.getCaloricStrategy().contains("Surplus"));

        DietPlan strengthDiet = DietPlan.forGoal(FitnessGoal.STRENGTH_TRAINING);
        assertNotNull(strengthDiet);
        assertTrue(strengthDiet.getTitle().contains("Strength"));

        DietPlan weightLossDiet = DietPlan.forGoal(FitnessGoal.WEIGHT_LOSS);
        assertNotNull(weightLossDiet);
        assertTrue(weightLossDiet.getTitle().contains("Fat Loss"));
        assertTrue(weightLossDiet.getCaloricStrategy().contains("Deficit"));
    }

    @Test
    @Order(13)
    @DisplayName("WeeklyProgressReport: Evaluates Targets, Completed Highlights, and Areas for Improvement")
    public void testWeeklyProgressReport() {
        FitnessGoal goal = new FitnessGoal(FitnessGoal.STRENGTH_TRAINING, 4, 180, 1500);
        LocalDate start = LocalDate.of(2026, 10, 19);
        LocalDate end = LocalDate.of(2026, 10, 25);

        WeeklyProgressReport partialReport = new WeeklyProgressReport(goal, start, end, 2, 90, 800);
        assertFalse(partialReport.isFrequencyCompleted());
        assertFalse(partialReport.isDurationCompleted());
        assertFalse(partialReport.isCaloriesCompleted());
        assertEquals(50, partialReport.getFrequencyPercent());
        assertEquals(50, partialReport.getDurationPercent());

        List<String> improvements = partialReport.getAreasNeedingImprovement();
        assertEquals(3, improvements.size());
        assertTrue(improvements.get(0).contains("Frequency"));

        WeeklyProgressReport completeReport = new WeeklyProgressReport(goal, start, end, 5, 200, 1650);
        assertTrue(completeReport.isFrequencyCompleted());
        assertTrue(completeReport.isDurationCompleted());
        assertTrue(completeReport.isCaloriesCompleted());
        assertEquals(100, completeReport.getFrequencyPercent());

        List<String> highlights = completeReport.getCompletedHighlights();
        assertEquals(3, highlights.size());
    }

    @Test
    @Order(14)
    @DisplayName("FitnessGoalDAO: Persists Goal and Evaluates Live Weekly Performance")
    public void testFitnessGoalPersistenceAndTracking() throws SQLException {
        FitnessGoal goal = new FitnessGoal(FitnessGoal.HYPERTROPHY, 3, 30, 150);
        boolean saved = goalDao.saveOrUpdateGoal(goal);
        assertTrue(saved);

        FitnessGoal fetched = goalDao.getGoal();
        assertNotNull(fetched);
        assertEquals(FitnessGoal.HYPERTROPHY, fetched.getGoalType());

        WeeklyProgressReport report = goalDao.getWeeklyProgressReport(LocalDate.of(2026, 10, 20));
        assertNotNull(report);
        assertTrue(report.getActualFrequency() >= 1);
        assertTrue(report.getActualDurationMins() > 0);
        assertTrue(report.getActualCalories() > 0);
    }
}
