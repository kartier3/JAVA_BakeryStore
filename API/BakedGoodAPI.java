package API;

import Models.BakedGood;
import Models.CustomList;
import Models.CustomHashTable;
import Models.Ingredient;
import Models.RecipeComponent;


public class BakedGoodAPI {
    public CustomList<BakedGood> bakedGoods;
    private CustomHashTable<String, BakedGood> byName;

    public BakedGoodAPI() {
        this.bakedGoods = new CustomList<>();
        this.byName = new CustomHashTable<>();
    }

    public void add(BakedGood bg) {
        bakedGoods.add(bg);
        byName.put(bg.getName().toLowerCase(), bg);
    }

    public BakedGood findByName(String name) {
        return byName.get(name.toLowerCase());
    }

    public void removeByName(String name) {
        BakedGood bg = byName.get(name.toLowerCase());
        if (bg == null) return;

        byName.remove(name.toLowerCase());

        for (int i = 0; i < bakedGoods.size(); i++) {
            if (bakedGoods.get(i) == bg) {
                bakedGoods.remove(i);
                return;
            }
        }
    }

    public CustomList<BakedGood> findByOrigin(String origin) {
        CustomList<BakedGood> result = new CustomList<>();
        for (int i = 0; i < bakedGoods.size(); i++) {
            BakedGood bg = bakedGoods.get(i);
            if (bg.getOrigin().equalsIgnoreCase(origin)) {
                result.add(bg);
            }
        }
        return result;
    }

    public CustomList<BakedGood> findByIngredient(Ingredient ingredient) {
        CustomList<BakedGood> result = new CustomList<>();
        for (int i = 0; i < bakedGoods.size(); i++) {
            BakedGood bg = bakedGoods.get(i);
            if (containsIngredient(bg, ingredient)) {
                result.add(bg);
            }
        }
        return result;
    }

    private boolean containsIngredient(BakedGood bg, Ingredient ingredient) {
        CustomList<RecipeComponent> recipe = bg.getRecipe();
        for (int i = 0; i < recipe.size(); i++) {
            if (recipe.get(i).getIngredient() == ingredient) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        bakedGoods.clear();
        byName = new CustomHashTable<>();
    }

}
