package Utils;

import Models.*;
import API.*;
import API.RecipeAPI;
import java.io.*;

public class PersistenceManager {

    private static final String INGREDIENTS_FILE = "ingredients.json";
    private static final String BAKEDGOODS_FILE = "bakedgoods.json";

    private IngredientAPI ingredientAPI;
    private BakedGoodAPI bakedGoodAPI;
    private RecipeAPI recipeAPI;

    public PersistenceManager(IngredientAPI ingredientAPI, BakedGoodAPI bakedGoodAPI, RecipeAPI recipeAPI) {
        this.ingredientAPI = ingredientAPI;
        this.bakedGoodAPI = bakedGoodAPI;
        this.recipeAPI = recipeAPI;
    }

    public void saveAll() {
        saveIngredients();
        saveBakedGoods();
    }

    private void saveIngredients() {
        String json = JsonUtils.toJsonArray(ingredientAPI.ingredients, recipeAPI);
        writeFile(INGREDIENTS_FILE, json);
    }

    private void saveBakedGoods() {
        String json = JsonUtils.toJsonArray(bakedGoodAPI.bakedGoods, recipeAPI);
        writeFile(BAKEDGOODS_FILE, json);
    }

    private void writeFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error saving to " + filename + ": " + e.getMessage());
        }
    }

    public void loadAll() {
        loadIngredients();
        loadBakedGoods();
    }

    private void loadIngredients() {
        String json = readFile(INGREDIENTS_FILE);
        if (json.isEmpty()) return;

        CustomList<String> items = JsonUtils.splitArrayItems(json);
        for (int i = 0; i < items.size(); i++) {
            Ingredient ing = JsonUtils.parseIngredient(items.get(i));
            ingredientAPI.add(ing);
        }
    }

    private void loadBakedGoods() {
        String json = readFile(BAKEDGOODS_FILE);
        if (json.isEmpty()) return;

        CustomList<String> items = JsonUtils.splitArrayItems(json);
        for (int i = 0; i < items.size(); i++) {
            BakedGood bg = parseBakedGood(items.get(i));
            bakedGoodAPI.add(bg);
        }
    }

    private BakedGood parseBakedGood(String json) {
        String name = JsonUtils.extractString(json, "name");
        String origin = JsonUtils.extractString(json, "origin");
        String description = JsonUtils.extractString(json, "description");
        String imageUrl = JsonUtils.extractString(json, "imageUrl");

        BakedGood bg = new BakedGood(name, origin, description, imageUrl);

        String recipeArray = JsonUtils.extractArray(json, "recipe");
        CustomList<String> components = JsonUtils.splitArrayItems(recipeArray);

        for (int i = 0; i < components.size(); i++) {
            String compJson = components.get(i);
            String ingName = JsonUtils.extractString(compJson, "ingredientName");
            double quantity = JsonUtils.extractDouble(compJson, "quantityGrams");

            Ingredient ing = ingredientAPI.findByName(ingName);
            if (ing != null) {
                recipeAPI.addIngredient(bg, ing, quantity);
            }
        }

        return bg;
    }

    private String readFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            System.err.println("Error loading from " + filename + ": " + e.getMessage());
        }
        return content.toString();
    }


    public boolean dataExists() {
        return new File(INGREDIENTS_FILE).exists() || new File(BAKEDGOODS_FILE).exists();
    }
}
