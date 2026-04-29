package Models;

public class Ingredient {
    private String name;
    private String description;
    private double caloriesPer100g;

    public Ingredient(String name, String description, double caloriesPer100g) {
        this.name = name;
        this.description = description;
        this.caloriesPer100g = caloriesPer100g;
    }

    public String getName() { 
        return name;
     }
    public void setName(String name) { 
        this.name = name; }

    public String getDescription() { 
        return description; }
    public void setDescription(String description) { 
        this.description = description; }

    public double getCaloriesPer100g() { 
        return caloriesPer100g; }
    public void setCaloriesPer100g(double caloriesPer100g) { 
        this.caloriesPer100g = caloriesPer100g; }

    @Override
    public String toString() {
        return name + " (" + caloriesPer100g + " cal/100g)";
    }
}
