package Models;

public class RecipeComponent {
    private Ingredient ingredient;
    private double quantityGrams;

    public RecipeComponent(Ingredient ingredient, double quantityGrams) {
        this.ingredient = ingredient;
        this.quantityGrams = quantityGrams;
    }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public double getQuantityGrams() { return quantityGrams; }
    public void setQuantityGrams(double quantityGrams) { this.quantityGrams = quantityGrams; }


    @Override
    public String toString() {
        return quantityGrams + "g of " + ingredient.getName();
    }
}
