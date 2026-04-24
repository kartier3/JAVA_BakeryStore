package API;

import Models.BakedGood;
import Models.Ingredient;
import Models.RecipeComponent;

public class RecipeAPI {

    public void addIngredient(BakedGood bakedGood, Ingredient ingredient, double quantityGrams) {
        RecipeComponent component = new RecipeComponent(ingredient, quantityGrams);
        bakedGood.getRecipe().add(component);
    }

    public double calculateCalories(RecipeComponent component) {
        return (component.getIngredient().getCaloriesPer100g() * component.getQuantityGrams()) / 100.0;
    }

    public double calculateTotalCalories(BakedGood bakedGood) {
        double total = 0;
        for (int i = 0; i < bakedGood.getRecipe().size(); i++) {
            total += calculateCalories(bakedGood.getRecipe().get(i));
        }
        return total;
    }

    public void removeIngredient(BakedGood bakedGood, int index) {
        bakedGood.getRecipe().remove(index);
    }

    public int getIngredientCount(BakedGood bakedGood) {
        return bakedGood.getRecipe().size();
    }

    public RecipeComponent getRecipeComponent(BakedGood bakedGood, int index) {
        return bakedGood.getRecipe().get(index);
    }
}
