package Tests;

import Models.*;
import API.*;
import Utils.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

public class AllTests {

    static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError(message);
        }
    }

    static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    static void assertSame(Object expected, Object actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected same reference: " + expected + ", Actual: " + actual);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Test {}

    @Test
    void testIngredientConstructor() {
        Ingredient flour = new Ingredient("Flour", "Wheat flour", 364);
        assertEquals("Flour", flour.getName(), "Name should be Flour");
        assertEquals("Wheat flour", flour.getDescription(), "Description should match");
        assertEquals(364, (int) flour.getCaloriesPer100g(), "Calories should be 364");
    }

    @Test
    void testIngredientSetters() {
        Ingredient flour = new Ingredient("Flour", "Wheat", 364);
        flour.setName("Bread Flour");
        flour.setCaloriesPer100g(360);
        assertEquals("Bread Flour", flour.getName(), "Name should be updated");
        assertEquals(360, (int) flour.getCaloriesPer100g(), "Calories should be updated");
    }

    @Test
    void testRecipeComponentLinksIngredient() {
        Ingredient butter = new Ingredient("Butter", "Dairy", 717);
        RecipeComponent comp = new RecipeComponent(butter, 50);
        assertSame(butter, comp.getIngredient(), "Should reference same ingredient");
        assertEquals(50, (int) comp.getQuantityGrams(), "Quantity should be 50");
    } 

    @Test
    void testRecipeComponentCanBeModified() {
        Ingredient sugar = new Ingredient("Sugar", "Granulated", 387);
        RecipeComponent comp = new RecipeComponent(sugar, 100);
        comp.setQuantityGrams(200);
        assertEquals(200, (int) comp.getQuantityGrams(), "Quantity should be updated");
    }

    @Test
    void testBakedGoodConstructor() {
        BakedGood croissant = new BakedGood("Croissant", "France", "Pastry", "c.jpg");
        assertEquals("Croissant", croissant.getName(), "Name should be Croissant");
        assertEquals("France", croissant.getOrigin(), "Origin should be France");
        assertTrue(croissant.getRecipe().isEmpty(), "Recipe should start empty");
    }

    @Test
    void testBakedGoodSetters() {
        BakedGood baguette = new BakedGood("Baguette", "France", "Bread", "b.jpg");
        baguette.setOrigin("French");
        assertEquals("French", baguette.getOrigin(), "Origin should be updated");
    }

    @Test
    void testCustomListAddAndGet() {
        CustomList<String> list = new CustomList<>();
        list.add("First");
        list.add("Second");
        assertEquals(2, list.size(), "Size should be 2");
        assertEquals("First", list.get(0), "First element");
        assertEquals("Second", list.get(1), "Second element");
    }

    @Test
    void testCustomListContains() {
        CustomList<String> list = new CustomList<>();
        list.add("Apple");
        assertTrue(list.contains("Apple"), "Should contain Apple");
    }

    @Test
    void testHashTablePutAndGet() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();
        table.put("Flour", 364);
        assertEquals(364, table.get("Flour"), "Should get 364 for Flour");
    }

    @Test
    void testHashTableSize() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();
        table.put("A", 1);
        table.put("B", 2);
        assertEquals(2, table.size(), "Size should be 2");
    }

    @Test
    void testInsertionSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        Sorting.insertionSort(arr);
        assertEquals(1, arr[0], "First should be 1");
        assertEquals(9, arr[4], "Last should be 9");
    }

    @Test
    void testQuickSort() {
        CustomList<String> list = new CustomList<>();
        list.add("Cherry");
        list.add("Apple");
        list.add("Banana");
        Sorting.quickSort(list);
        assertEquals("Apple", list.get(0), "First should be Apple");
    }

    @Test
    void testIngredientAPIFindByName() {
        IngredientAPI api = new IngredientAPI();
        api.add(new Ingredient("Flour", "Wheat", 364));

        Ingredient found = api.findByName("Flour");
        assertNotNull(found, "Should find Flour");
        assertEquals("Flour", found.getName(), "Name should match");
    }

    @Test
    void testBakedGoodAPIFindByOrigin() {
        BakedGoodAPI api = new BakedGoodAPI();
        api.add(new BakedGood("Croissant", "France", "Pastry", "c.jpg"));
        api.add(new BakedGood("Baguette", "France", "Bread", "b.jpg"));

        var french = api.findByOrigin("France");
        assertEquals(2, french.size(), "Should find 2 French items");
    }

    @Test
    void testBakedGoodAPIFindByIngredient() {
        BakedGoodAPI bgApi = new BakedGoodAPI();
        RecipeAPI recipeApi = new RecipeAPI();

        Ingredient butter = new Ingredient("Butter", "Dairy", 717);
        BakedGood croissant = new BakedGood("Croissant", "France", "Pastry", "c.jpg");
        BakedGood cookie = new BakedGood("Cookie", "USA", "Dessert", "cookie.jpg");

        bgApi.add(croissant);
        bgApi.add(cookie);

        recipeApi.addIngredient(croissant, butter, 50);
        recipeApi.addIngredient(cookie, butter, 30);

        var withButter = bgApi.findByIngredient(butter);
        assertEquals(2, withButter.size(), "Should find 2 items with butter");
    }

    @Test
    void testRecipeAPICalories() {
        RecipeAPI api = new RecipeAPI();
        Ingredient butter = new Ingredient("Butter", "Dairy", 717);
        BakedGood croissant = new BakedGood("Croissant", "France", "Pastry", "c.jpg");

        api.addIngredient(croissant, butter, 50);
        double cals = api.calculateCalories(croissant.getRecipe().get(0));
        assertEquals(358.5, cals, 0.001, "Calories should be 358.5");
    }

    @Test
    void testJsonSerialization() {
        Ingredient flour = new Ingredient("Flour", "Wheat flour", 364);
        String json = JsonUtils.toJson(flour);
        assertTrue(json.contains("\"name\": \"Flour\""), "JSON should contain name");
        assertTrue(json.contains("\"caloriesPer100g\": 364.0"), "JSON should contain calories");
    }

    @Test
    void testJsonDeserialization() {
        String json = "{\"type\": \"ingredient\", \"name\": \"Sugar\", \"description\": \"Sweet\", \"caloriesPer100g\": 387.0}";
        Ingredient ing = JsonUtils.parseIngredient(json);
        assertEquals("Sugar", ing.getName(), "Name should be parsed");
        assertEquals("Sweet", ing.getDescription(), "Description should be parsed");
        assertEquals(387, (int) ing.getCaloriesPer100g(), "Calories should be parsed");
    }

    public static void main(String[] args) {
        AllTests tests = new AllTests();
        int passed = 0;
        int failed = 0;

        System.out.println("========================================");
        System.out.println("           RUNNING TESTS");
        System.out.println("========================================\n");

        java.lang.reflect.Method[] methods = AllTests.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                try {
                    method.invoke(tests);
                    System.out.println("PASS: " + method.getName());
                    passed++;
                } catch (Exception e) {
                    System.out.println("FAIL: " + method.getName() + " - " + e.getCause().getMessage());
                    failed++;
                }
            }
        }

        System.out.println("\n========================================");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("========================================");
    }

}
