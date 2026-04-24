package Utils;

import Models.*;
import API.RecipeAPI;


public class JsonUtils {


    public static String toJson(Ingredient ing) {
        return "{\n" +
               "  \"type\": \"ingredient\",\n" +
               "  \"name\": \"" + escape(ing.getName()) + "\",\n" +
               "  \"description\": \"" + escape(ing.getDescription()) + "\",\n" +
               "  \"caloriesPer100g\": " + ing.getCaloriesPer100g() + "\n" +
               "}";
    }

    public static String toJson(BakedGood bg, RecipeAPI recipeApi) {
        String recipeJson = recipeToJson(bg.getRecipe());

        return "{\n" +
               "  \"type\": \"bakedGood\",\n" +
               "  \"name\": \"" + escape(bg.getName()) + "\",\n" +
               "  \"origin\": \"" + escape(bg.getOrigin()) + "\",\n" +
               "  \"description\": \"" + escape(bg.getDescription()) + "\",\n" +
               "  \"imageUrl\": \"" + escape(bg.getImageUrl()) + "\",\n" +
               "  \"recipe\": [\n" +
               recipeJson +
               "  ]\n" +
               "}";
    }

    private static String recipeToJson(CustomList<RecipeComponent> recipe) {
        if (recipe.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < recipe.size(); i++) {
            RecipeComponent comp = recipe.get(i);
            result.append("    {\n");
            result.append("      \"ingredientName\": \"" + escape(comp.getIngredient().getName()) + "\",\n");
            result.append("      \"quantityGrams\": " + comp.getQuantityGrams() + "\n");
            result.append("    }");
            if (i < recipe.size() - 1) {
                result.append(",");
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static String toJsonArray(CustomList<?> list, RecipeAPI recipeApi) {
        String items = arrayItemsToJson(list, recipeApi);
        return "[\n" + items + "]";
    }

    private static String arrayItemsToJson(CustomList<?> list, RecipeAPI recipeApi) {
        if (list.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof Ingredient) {
                result.append(toJson((Ingredient) item));
            } else if (item instanceof BakedGood) {
                result.append(toJson((BakedGood) item, recipeApi));
            }
            if (i < list.size() - 1) {
                result.append(",");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ============ DESERIALIZATION (JSON String -> Object) ============

    public static Ingredient parseIngredient(String json) {
        String name = extractString(json, "name");
        String description = extractString(json, "description");
        double calories = extractDouble(json, "caloriesPer100g");
        return new Ingredient(name, description, calories);
    }

    public static String extractString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start = json.indexOf("\"", start + pattern.length() - 1) + 1;
        StringBuilder result = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == 'n') result.append('\n');
                else if (next == 't') result.append('\t');
                else if (next == 'r') result.append('\r');
                else result.append(next);
                i++;
            } else if (c == '"') {
                break;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static double extractDouble(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*";
        int start = json.indexOf(pattern);
        if (start == -1) return 0;
        start = start + pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) {
            end++;
        }
        try {
            return Double.parseDouble(json.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int extractInt(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*";
        int start = json.indexOf(pattern);
        if (start == -1) return 0;
        start = start + pattern.length();
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String extractArray(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\[";
        int start = json.indexOf(pattern);
        if (start == -1) return "[]";
        start = start + pattern.length() - 1;
        int braceCount = 0;
        int end = start;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') braceCount++;
            else if (c == ']') {
                braceCount--;
                if (braceCount == 0) {
                    end = i + 1;
                    break;
                }
            }
        }
        return json.substring(start, end);
    }

    public static CustomList<String> splitArrayItems(String arrayJson) {
        CustomList<String> items = new CustomList<>();
        if (arrayJson.equals("[]")) return items;

        int braceCount = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 1; i < arrayJson.length() - 1; i++) {
            char c = arrayJson.charAt(i);
            if (c == '{') {
                braceCount++;
                current.append(c);
            } else if (c == '}') {
                braceCount--;
                current.append(c);
                if (braceCount == 0) {
                    items.add(current.toString().trim());
                    current = new StringBuilder();
                    i++;
                }
            } else if (braceCount > 0) {
                current.append(c);
            }
        }
        return items;
    }
}
