package com.workouttracker.ui;

import com.workouttracker.database.FitnessGoalDAO;
import com.workouttracker.model.DietPlan;
import com.workouttracker.model.FitnessGoal;
import com.workouttracker.model.WeeklyProgressReport;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoalsPanel extends JPanel {

    private final FitnessGoalDAO goalDAO;
    private final Runnable onGoalUpdatedCallback;

    private JLabel lblGoalBadge;
    private JLabel lblWeekRange;

    private JLabel lblFreqValue;
    private JProgressBar barFreq;
    private JLabel lblFreqStatus;

    private JLabel lblDurationValue;
    private JProgressBar barDuration;
    private JLabel lblDurationStatus;

    private JLabel lblCaloriesValue;
    private JProgressBar barCalories;
    private JLabel lblCaloriesStatus;

    private JPanel completedPanel;
    private JPanel improvementPanel;

    private JLabel lblDietTitle;
    private JLabel lblCaloricStrategy;
    private JLabel lblMacroRatio;
    private JLabel lblDailyCalories;
    private JTextArea txtMealPlan;
    private JTextArea txtHydrationTips;

    public GoalsPanel(FitnessGoalDAO goalDAO, Runnable onGoalUpdatedCallback) {
        this.goalDAO = goalDAO;
        this.onGoalUpdatedCallback = onGoalUpdatedCallback;

        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        initUI();
        refreshGoalsView();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("Personal Fitness Goals & Weekly Progress Tracking");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));

        lblWeekRange = new JLabel("Current Week Tracking");
        lblWeekRange.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWeekRange.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblWeekRange);
        headerPanel.add(titleBox, BorderLayout.WEST);

        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBox.setOpaque(false);

        lblGoalBadge = new JLabel("Goal: Hypertrophy");
        lblGoalBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGoalBadge.setForeground(new Color(37, 99, 235));
        lblGoalBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JButton btnConfigure = UIUtils.createPrimaryButton("⚙ Configure Weekly Goals");
        btnConfigure.addActionListener(e -> openGoalConfigurationDialog());

        actionBox.add(lblGoalBadge);
        actionBox.add(btnConfigure);
        headerPanel.add(actionBox, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTrackingPanel(), createDietPanel());
        splitPane.setResizeWeight(0.52);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTrackingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(203, 213, 225), 1),
                        "  Weekly Target vs Actual Performance  ", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), new Color(30, 41, 59)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel cardsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        cardsPanel.setOpaque(false);

        cardsPanel.add(createFrequencyCard());
        cardsPanel.add(createDurationCard());
        cardsPanel.add(createCaloriesCard());

        panel.add(cardsPanel, BorderLayout.NORTH);

        JPanel reportsContainer = new JPanel(new GridLayout(2, 1, 0, 8));
        reportsContainer.setOpaque(false);
        reportsContainer.setBorder(new EmptyBorder(6, 0, 0, 0));

        completedPanel = new JPanel(new BorderLayout(0, 4));
        completedPanel.setBackground(new Color(240, 253, 244));
        completedPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        reportsContainer.add(completedPanel);

        improvementPanel = new JPanel(new BorderLayout(0, 4));
        improvementPanel.setBackground(new Color(254, 252, 232));
        improvementPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(254, 240, 138), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        reportsContainer.add(improvementPanel);

        panel.add(reportsContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFrequencyCard() {
        JPanel card = new JPanel(new BorderLayout(6, 4));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Workout Frequency (Days/Week)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(30, 41, 59));

        lblFreqValue = new JLabel("0 / 4 days");
        lblFreqValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFreqValue.setForeground(new Color(37, 99, 235));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblFreqValue, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        barFreq = new JProgressBar(0, 100);
        barFreq.setValue(0);
        barFreq.setStringPainted(true);
        barFreq.setFont(new Font("Segoe UI", Font.BOLD, 11));
        barFreq.setForeground(new Color(34, 197, 94));
        barFreq.setBackground(new Color(226, 232, 240));
        barFreq.setPreferredSize(new Dimension(0, 16));
        card.add(barFreq, BorderLayout.CENTER);

        lblFreqStatus = new JLabel("Status: 0% completed");
        lblFreqStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFreqStatus.setForeground(new Color(100, 116, 139));
        card.add(lblFreqStatus, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDurationCard() {
        JPanel card = new JPanel(new BorderLayout(6, 4));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Workout Duration (Minutes/Week)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(30, 41, 59));

        lblDurationValue = new JLabel("0 / 180 mins");
        lblDurationValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDurationValue.setForeground(new Color(37, 99, 235));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblDurationValue, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        barDuration = new JProgressBar(0, 100);
        barDuration.setValue(0);
        barDuration.setStringPainted(true);
        barDuration.setFont(new Font("Segoe UI", Font.BOLD, 11));
        barDuration.setForeground(new Color(59, 130, 246));
        barDuration.setBackground(new Color(226, 232, 240));
        barDuration.setPreferredSize(new Dimension(0, 16));
        card.add(barDuration, BorderLayout.CENTER);

        lblDurationStatus = new JLabel("Status: 0% completed");
        lblDurationStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDurationStatus.setForeground(new Color(100, 116, 139));
        card.add(lblDurationStatus, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCaloriesCard() {
        JPanel card = new JPanel(new BorderLayout(6, 4));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Calories Burned (kcal/Week)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(30, 41, 59));

        lblCaloriesValue = new JLabel("0 / 1500 kcal");
        lblCaloriesValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCaloriesValue.setForeground(new Color(37, 99, 235));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblCaloriesValue, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        barCalories = new JProgressBar(0, 100);
        barCalories.setValue(0);
        barCalories.setStringPainted(true);
        barCalories.setFont(new Font("Segoe UI", Font.BOLD, 11));
        barCalories.setForeground(new Color(249, 115, 22));
        barCalories.setBackground(new Color(226, 232, 240));
        barCalories.setPreferredSize(new Dimension(0, 16));
        card.add(barCalories, BorderLayout.CENTER);

        lblCaloriesStatus = new JLabel("Status: 0% completed");
        lblCaloriesStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCaloriesStatus.setForeground(new Color(100, 116, 139));
        card.add(lblCaloriesStatus, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDietPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(203, 213, 225), 1),
                        "  Personalized Goal-Specific Diet Plan  ", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), new Color(30, 41, 59)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel summaryCard = new JPanel(new GridLayout(4, 1, 0, 4));
        summaryCard.setBackground(new Color(248, 250, 252));
        summaryCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        lblDietTitle = new JLabel("Diet Protocol");
        lblDietTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDietTitle.setForeground(new Color(30, 41, 59));

        lblCaloricStrategy = new JLabel("Strategy:");
        lblCaloricStrategy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCaloricStrategy.setForeground(new Color(71, 85, 105));

        lblMacroRatio = new JLabel("Macros:");
        lblMacroRatio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMacroRatio.setForeground(new Color(71, 85, 105));

        lblDailyCalories = new JLabel("Daily Calories:");
        lblDailyCalories.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDailyCalories.setForeground(new Color(37, 99, 235));

        summaryCard.add(lblDietTitle);
        summaryCard.add(lblCaloricStrategy);
        summaryCard.add(lblMacroRatio);
        summaryCard.add(lblDailyCalories);

        panel.add(summaryCard, BorderLayout.NORTH);

        JPanel mealPlanContainer = new JPanel(new BorderLayout(0, 6));
        mealPlanContainer.setOpaque(false);

        JLabel lblMealTitle = new JLabel("Sample Daily Meal Distribution:");
        lblMealTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMealTitle.setForeground(new Color(30, 41, 59));
        mealPlanContainer.add(lblMealTitle, BorderLayout.NORTH);

        txtMealPlan = new JTextArea();
        txtMealPlan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMealPlan.setLineWrap(true);
        txtMealPlan.setWrapStyleWord(true);
        txtMealPlan.setEditable(false);
        txtMealPlan.setBackground(Color.WHITE);
        txtMealPlan.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scrollMeals = new JScrollPane(txtMealPlan);
        scrollMeals.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        mealPlanContainer.add(scrollMeals, BorderLayout.CENTER);

        JPanel hydrationBox = new JPanel(new BorderLayout(0, 2));
        hydrationBox.setOpaque(false);
        JLabel lblHydrationHeader = new JLabel("Hydration & Training Tips:");
        lblHydrationHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHydrationHeader.setForeground(new Color(30, 41, 59));

        txtHydrationTips = new JTextArea(3, 0);
        txtHydrationTips.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtHydrationTips.setLineWrap(true);
        txtHydrationTips.setWrapStyleWord(true);
        txtHydrationTips.setEditable(false);
        txtHydrationTips.setBackground(new Color(241, 245, 249));
        txtHydrationTips.setBorder(new EmptyBorder(4, 6, 4, 6));

        hydrationBox.add(lblHydrationHeader, BorderLayout.NORTH);
        hydrationBox.add(txtHydrationTips, BorderLayout.CENTER);
        mealPlanContainer.add(hydrationBox, BorderLayout.SOUTH);

        panel.add(mealPlanContainer, BorderLayout.CENTER);

        return panel;
    }

    public void refreshGoalsView() {
        try {
            LocalDate today = LocalDate.now();
            WeeklyProgressReport report = goalDAO.getWeeklyProgressReport(today);
            FitnessGoal goal = report.getGoal();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd");
            lblWeekRange.setText("Week: " + report.getWeekStart().format(fmt) + " - " + report.getWeekEnd().format(fmt) + ", " + today.getYear());
            lblGoalBadge.setText("Goal: " + goal.getGoalType().toUpperCase());

            int freqPct = report.getFrequencyPercent();
            lblFreqValue.setText(report.getActualFrequency() + " / " + report.getTargetFrequency() + " days");
            barFreq.setValue(freqPct);
            lblFreqStatus.setText(report.isFrequencyCompleted() ? "✔ Completed target frequency!" : (report.getTargetFrequency() - report.getActualFrequency()) + " day(s) remaining");

            int durPct = report.getDurationPercent();
            lblDurationValue.setText(report.getActualDurationMins() + " / " + report.getTargetDurationMins() + " mins");
            barDuration.setValue(durPct);
            lblDurationStatus.setText(report.isDurationCompleted() ? "✔ Completed target duration!" : (report.getTargetDurationMins() - report.getActualDurationMins()) + " mins remaining");

            int calPct = report.getCaloriesPercent();
            lblCaloriesValue.setText(report.getActualCalories() + " / " + report.getTargetCalories() + " kcal");
            barCalories.setValue(calPct);
            lblCaloriesStatus.setText(report.isCaloriesCompleted() ? "✔ Completed calorie burn target!" : (report.getTargetCalories() - report.getActualCalories()) + " kcal remaining");

            updateReportPanels(report);

            DietPlan diet = DietPlan.forGoal(goal.getGoalType());
            lblDietTitle.setText(diet.getTitle());
            lblCaloricStrategy.setText("Strategy: " + diet.getCaloricStrategy());
            lblMacroRatio.setText("Macro Ratio: " + diet.getMacroDistribution());
            lblDailyCalories.setText("Estimated Daily Target: " + diet.getEstimatedDailyCalories());

            StringBuilder sb = new StringBuilder();
            sb.append("• BREAKFAST:\n  ").append(diet.getBreakfast()).append("\n\n");
            sb.append("• LUNCH:\n  ").append(diet.getLunch()).append("\n\n");
            sb.append("• PRE-WORKOUT:\n  ").append(diet.getPreWorkout()).append("\n\n");
            sb.append("• POST-WORKOUT:\n  ").append(diet.getPostWorkout()).append("\n\n");
            sb.append("• DINNER:\n  ").append(diet.getDinner());
            txtMealPlan.setText(sb.toString());
            txtMealPlan.setCaretPosition(0);

            txtHydrationTips.setText(diet.getHydrationAndTips());
            txtHydrationTips.setCaretPosition(0);

        } catch (SQLException e) {
            System.err.println("Error refreshing goals view: " + e.getMessage());
        }
    }

    private void updateReportPanels(WeeklyProgressReport report) {
        completedPanel.removeAll();
        JPanel compContent = new JPanel();
        compContent.setLayout(new BoxLayout(compContent, BoxLayout.Y_AXIS));
        compContent.setOpaque(false);

        JLabel compTitle = new JLabel("✔  Completed Targets This Week:");
        compTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        compTitle.setForeground(new Color(22, 101, 52));
        compContent.add(compTitle);
        compContent.add(Box.createVerticalStrut(4));

        List<String> completed = report.getCompletedHighlights();
        for (String c : completed) {
            JLabel lbl = new JLabel("• " + c);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(new Color(22, 101, 52));
            compContent.add(lbl);
            compContent.add(Box.createVerticalStrut(2));
        }
        completedPanel.add(compContent, BorderLayout.CENTER);

        improvementPanel.removeAll();
        JPanel impContent = new JPanel();
        impContent.setLayout(new BoxLayout(impContent, BoxLayout.Y_AXIS));
        impContent.setOpaque(false);

        JLabel impTitle = new JLabel("⚠  Areas Needing Improvement:");
        impTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        impTitle.setForeground(new Color(133, 77, 14));
        impContent.add(impTitle);
        impContent.add(Box.createVerticalStrut(4));

        List<String> areas = report.getAreasNeedingImprovement();
        for (String a : areas) {
            JLabel lbl = new JLabel("• " + a);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(new Color(133, 77, 14));
            impContent.add(lbl);
            impContent.add(Box.createVerticalStrut(2));
        }
        improvementPanel.add(impContent, BorderLayout.CENTER);

        completedPanel.revalidate();
        completedPanel.repaint();
        improvementPanel.revalidate();
        improvementPanel.repaint();
    }

    public void openGoalConfigurationDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Configure Weekly Fitness Goals", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 380);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel("Set Your Personal Fitness Goal & Targets");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(30, 41, 59));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 12));
        formPanel.setOpaque(false);

        JLabel lblGoalType = new JLabel("Primary Goal:*");
        lblGoalType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String[] goalOptions = {FitnessGoal.HYPERTROPHY, FitnessGoal.STRENGTH_TRAINING, FitnessGoal.WEIGHT_LOSS};
        JComboBox<String> cmbGoal = new JComboBox<>(goalOptions);
        cmbGoal.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblFreq = new JLabel("Target Frequency (days/week):*");
        lblFreq.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JSpinner spinFreq = new JSpinner(new SpinnerNumberModel(4, 1, 7, 1));
        spinFreq.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblDuration = new JLabel("Target Duration (mins/week):*");
        lblDuration.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JSpinner spinDuration = new JSpinner(new SpinnerNumberModel(180, 15, 1200, 15));
        spinDuration.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblCalories = new JLabel("Target Calories Burned (kcal/week):*");
        lblCalories.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JSpinner spinCalories = new JSpinner(new SpinnerNumberModel(1500, 100, 10000, 50));
        spinCalories.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        try {
            FitnessGoal current = goalDAO.getGoal();
            if (current != null) {
                cmbGoal.setSelectedItem(current.getGoalType());
                spinFreq.setValue(current.getTargetFrequency());
                spinDuration.setValue(current.getTargetDurationMins());
                spinCalories.setValue(current.getTargetCalories());
            }
        } catch (SQLException ignored) {}

        cmbGoal.addActionListener(e -> {
            String selected = (String) cmbGoal.getSelectedItem();
            if (FitnessGoal.WEIGHT_LOSS.equals(selected)) {
                spinFreq.setValue(5);
                spinDuration.setValue(240);
                spinCalories.setValue(2200);
            } else if (FitnessGoal.STRENGTH_TRAINING.equals(selected)) {
                spinFreq.setValue(4);
                spinDuration.setValue(200);
                spinCalories.setValue(1800);
            } else {
                spinFreq.setValue(4);
                spinDuration.setValue(180);
                spinCalories.setValue(1500);
            }
        });

        formPanel.add(lblGoalType);
        formPanel.add(cmbGoal);
        formPanel.add(lblFreq);
        formPanel.add(spinFreq);
        formPanel.add(lblDuration);
        formPanel.add(spinDuration);
        formPanel.add(lblCalories);
        formPanel.add(spinCalories);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = UIUtils.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSave = UIUtils.createPrimaryButton("Save Goal & Update Diet");
        btnSave.addActionListener(e -> {
            String goalType = (String) cmbGoal.getSelectedItem();
            int freq = (int) spinFreq.getValue();
            int duration = (int) spinDuration.getValue();
            int calories = (int) spinCalories.getValue();

            FitnessGoal goal = new FitnessGoal(goalType, freq, duration, calories);
            try {
                goalDAO.saveOrUpdateGoal(goal);
                refreshGoalsView();
                if (onGoalUpdatedCallback != null) {
                    onGoalUpdatedCallback.run();
                }
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                        "Goal updated to " + goalType + " with personalized diet plan!",
                        "Goal Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error saving goal: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
