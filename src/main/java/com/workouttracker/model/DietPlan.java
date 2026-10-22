package com.workouttracker.model;

public class DietPlan {

    private final String goalType;
    private final String title;
    private final String caloricStrategy;
    private final String macroDistribution;
    private final String estimatedDailyCalories;
    private final String breakfast;
    private final String lunch;
    private final String preWorkout;
    private final String postWorkout;
    private final String dinner;
    private final String hydrationAndTips;

    public DietPlan(String goalType, String title, String caloricStrategy, String macroDistribution,
                    String estimatedDailyCalories, String breakfast, String lunch,
                    String preWorkout, String postWorkout, String dinner, String hydrationAndTips) {
        this.goalType = goalType;
        this.title = title;
        this.caloricStrategy = caloricStrategy;
        this.macroDistribution = macroDistribution;
        this.estimatedDailyCalories = estimatedDailyCalories;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.preWorkout = preWorkout;
        this.postWorkout = postWorkout;
        this.dinner = dinner;
        this.hydrationAndTips = hydrationAndTips;
    }

    public static DietPlan forGoal(String goalType) {
        if (goalType == null) {
            goalType = FitnessGoal.HYPERTROPHY;
        }

        switch (goalType.trim()) {
            case FitnessGoal.STRENGTH_TRAINING:
                return new DietPlan(
                        FitnessGoal.STRENGTH_TRAINING,
                        "Strength & Neuromuscular Power Diet",
                        "Energy-Dense Maintenance / Slight Surplus (+200-300 kcal)",
                        "Protein: 25% (1.8-2.0g/kg) | Carbohydrates: 50% | Fats: 25%",
                        "2,700 - 3,100 kcal / day",
                        "4 Whole eggs scrambled with cheddar cheese, 2 slices whole-grain toast, 1 banana, and a glass of milk.",
                        "Lean ground beef (90/10) with quinoa, black beans, avocado slices, and roasted carrots.",
                        "Cream of rice with whey protein powder, 1 tbsp almond butter, and pinch of sea salt (60m prior).",
                        "Chocolate milk or 1 scoop whey isolate with 2 plain bagels and honey for rapid glycogen refill.",
                        "Ribeye or flank steak with baked sweet potato, asparagus, and garlic olive oil.",
                        "Drink 3.5 - 4.5 Liters of water daily. Ensure adequate sodium and magnesium for muscle contractions."
                );

            case FitnessGoal.WEIGHT_LOSS:
                return new DietPlan(
                        FitnessGoal.WEIGHT_LOSS,
                        "Fat Loss & Lean Muscle Preservation Diet",
                        "Controlled Caloric Deficit (-400 to -500 kcal below maintenance)",
                        "Protein: 35% (2.0-2.4g/kg) | Carbohydrates: 40% | Fats: 25%",
                        "1,800 - 2,100 kcal / day",
                        "Egg white omelet (4 whites + 1 whole egg) with spinach, bell peppers, mushrooms, and half an avocado.",
                        "Grilled chicken breast (200g) over large mixed green salad, cherry tomatoes, cucumbers, and olive oil vinaigrette.",
                        "1 Medium green apple with 1 tbsp peanut butter, or 1 cup black coffee.",
                        "1 Scoop whey protein isolate mixed in cold water with 2 rice cakes.",
                        "Baked white fish (tilapia/cod) or grilled tofu with steamed broccoli, cauliflower rice, and lemon zest.",
                        "Drink at least 3.5 Liters of water. Drink 500ml before meals to maximize fullness. Prioritize 7-8 hours sleep."
                );

            case FitnessGoal.HYPERTROPHY:
            default:
                return new DietPlan(
                        FitnessGoal.HYPERTROPHY,
                        "Hypertrophy & Muscle Synthesis Diet",
                        "Lean Muscle Surplus (+300 to +400 kcal above maintenance)",
                        "Protein: 30% (1.8-2.2g/kg) | Carbohydrates: 45% | Fats: 25%",
                        "2,500 - 2,850 kcal / day",
                        "1 cup rolled oats cooked in almond milk, 1 scoop vanilla whey protein, 1 sliced banana, and 2 tbsp peanut butter.",
                        "Grilled chicken breast (180g) with 1.5 cups jasmine rice, steamed broccoli florets, and 1 tbsp olive oil.",
                        "Greek yogurt (0% fat) with mixed berries, a drizzle of raw honey, and a handful of almonds.",
                        "Whey protein shake (30g protein) blended with a banana, 5g creatine monohydrate, and rice krispies.",
                        "Baked Atlantic salmon fillet (200g) with baked sweet potato cubes and roasted green beans.",
                        "Drink 3.5 - 4.0 Liters of water daily. Distribute protein intake across 4-5 meals spaced 3-4 hours apart."
                );
        }
    }

    public String getGoalType() {
        return goalType;
    }

    public String getTitle() {
        return title;
    }

    public String getCaloricStrategy() {
        return caloricStrategy;
    }

    public String getMacroDistribution() {
        return macroDistribution;
    }

    public String getEstimatedDailyCalories() {
        return estimatedDailyCalories;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public String getPreWorkout() {
        return preWorkout;
    }

    public String getPostWorkout() {
        return postWorkout;
    }

    public String getDinner() {
        return dinner;
    }

    public String getHydrationAndTips() {
        return hydrationAndTips;
    }
}
