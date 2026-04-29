package API;

import Models.CustomList;
import Models.Ingredient;
import Models.CustomHashTable;

public class IngredientAPI {
    public CustomList<Ingredient> ingredients;
    private CustomHashTable<String, Ingredient> byName;

    public IngredientAPI() {
        this.ingredients = new CustomList<>();
        this.byName = new CustomHashTable<>();
    }

    public void add(Ingredient ing) {
        ingredients.add(ing);
        byName.put(ing.getName().toLowerCase(), ing);
    }

    public Ingredient findByName(String name) {
        return byName.get(name.toLowerCase());
    }

    public void removeByName(String name) {
        Ingredient ing = byName.get(name.toLowerCase());
        if (ing == null) return;

        byName.remove(name.toLowerCase());

        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i) == ing) {
                ingredients.remove(i);
                return;
            }
        }
    }

    public void clear() {
        ingredients.clear();
        byName = new CustomHashTable<>();
    }

}
