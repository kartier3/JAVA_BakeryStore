package Models;

public class BakedGood {
    private String name;
    private String origin;
    private String description;
    private String imageUrl;
    private CustomList<RecipeComponent> recipe;

    public BakedGood(String name, String origin, String description, String imageUrl) {
        this.name = name;
        this.origin = origin;
        this.description = description;
        this.imageUrl = imageUrl;
        this.recipe = new CustomList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; }

    public String getOrigin() {
         return origin; }
    public void setOrigin(String origin) { 
        this.origin = origin; }

    public String getDescription() {
         return description; }
    public void setDescription(String description) { 
        this.description = description; }

    public String getImageUrl() { 
        return imageUrl; }
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; }

    public CustomList<RecipeComponent> getRecipe() { 
        return recipe; }


    @Override
    public String toString() {
        return name + " (" + origin + ")";
    }
}
