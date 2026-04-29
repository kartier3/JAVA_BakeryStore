# Bakery Store

Bakery Store is a Java Swing desktop application for managing baked goods, ingredients, and recipes. It lets users add, edit, remove, search, load, and save bakery data using local JSON files.

## Features

- Manage baked goods with name, origin, description, and image URL.
- Manage ingredients with name, description, and calories per 100g.
- Build recipes by adding ingredients and quantities in grams.
- Calculate total calories for each baked good recipe.
- Search baked goods by name or origin.
- Save and load data from `ingredients.json` and `bakedgoods.json`.
- Includes custom data structures and sorting utilities.

## Project Structure

```text
API/       Application logic for baked goods, ingredients, recipes, and sorting
GUI/       Swing user interface and application entry point
Models/    Domain models and custom data structures
Utils/     JSON and persistence utilities
Tests/     Lightweight test runner
```

## Requirements

- Java Development Kit (JDK) 11 or newer

## Run the Application

From the project root:

```bash
javac API/*.java GUI/*.java Models/*.java Utils/*.java Tests/*.java
java GUI.MainApp
```

## Run Tests

From the project root:

```bash
javac API/*.java GUI/*.java Models/*.java Utils/*.java Tests/*.java
java Tests.AllTests
```

## Data Files

The application stores saved data in:

- `ingredients.json`
- `bakedgoods.json`

If these files do not exist, the application starts with sample data.

## Main Classes

- `GUI.MainApp` - starts the Swing application.
- `API.BakedGoodAPI` - manages baked goods and search operations.
- `API.IngredientAPI` - manages ingredients.
- `API.RecipeAPI` - manages recipe components and calorie calculations.
- `Utils.PersistenceManager` - saves and loads JSON data.
- `Tests.AllTests` - runs the project tests.
