package GUI;

import API.BakedGoodAPI;
import API.IngredientAPI;
import API.RecipeAPI;
import Models.BakedGood;
import Models.Ingredient;
import Models.RecipeComponent;
import Utils.PersistenceManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

public class MainApp extends JFrame {
    private final IngredientAPI ingredientAPI = new IngredientAPI();
    private final BakedGoodAPI bakedGoodAPI = new BakedGoodAPI();
    private final RecipeAPI recipeAPI = new RecipeAPI();
    private final PersistenceManager persistenceManager =
            new PersistenceManager(ingredientAPI, bakedGoodAPI, recipeAPI);

    private final DefaultListModel<Ingredient> ingredientListModel = new DefaultListModel<>();
    private final DefaultListModel<BakedGood> bakedGoodListModel = new DefaultListModel<>();
    private final DefaultListModel<RecipeComponent> recipeListModel = new DefaultListModel<>();
    private final DefaultComboBoxModel<Ingredient> ingredientComboModel = new DefaultComboBoxModel<>();

    private final JList<Ingredient> ingredientList = new JList<>(ingredientListModel);
    private final JList<BakedGood> bakedGoodList = new JList<>(bakedGoodListModel);
    private final JList<RecipeComponent> recipeList = new JList<>(recipeListModel);
    private final JComboBox<Ingredient> recipeIngredientCombo = new JComboBox<>(ingredientComboModel);

    private final JTextField ingredientNameField = new JTextField();
    private final JTextField ingredientCaloriesField = new JTextField();
    private final JTextArea ingredientDescriptionArea = new JTextArea(4, 36);

    private final JTextField bakedGoodNameField = new JTextField();
    private final JTextField bakedGoodOriginField = new JTextField();
    private final JTextField bakedGoodImageField = new JTextField();
    private final JTextArea bakedGoodDescriptionArea = new JTextArea(5, 36);
    private final JTextField recipeQuantityField = new JTextField();
    private final JTextField searchField = new JTextField(18);
    private final JLabel totalCaloriesLabel = new JLabel("Total calories: 0");

    private final DecimalFormat numberFormat = new DecimalFormat("0.##");

    public MainApp() {
        super("Bakery Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 620));

        loadInitialData();
        buildLayout();
        refreshAll();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Baked goods", buildBakedGoodsPanel());
        tabs.addTab("Ingredients", buildIngredientsPanel());
        root.add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel title = new JLabel("Bakery Store Manager");
        title.setFont(title.getFont().deriveFont(20f));
        panel.add(title, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton loadButton = new JButton("Load");
        JButton saveButton = new JButton("Save");

        loadButton.addActionListener(event -> {
            loadData();
            refreshAll();
            showMessage("Data loaded.");
        });
        saveButton.addActionListener(event -> {
            persistenceManager.saveAll();
            showMessage("Data saved.");
        });

        buttons.add(loadButton);
        buttons.add(saveButton);
        panel.add(buttons, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedIngredient();
            }
        });
        panel.add(new JScrollPane(ingredientList), BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Ingredient details"));
        addLabelAndField(form, "Name", ingredientNameField, 0);
        addLabelAndField(form, "Calories per 100g", ingredientCaloriesField, 1);
        addLabelAndArea(form, "Description", ingredientDescriptionArea, 2);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addButton = new JButton("Add ingredient");
        JButton updateButton = new JButton("Update selected");
        JButton removeButton = new JButton("Remove selected");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(event -> addIngredient());
        updateButton.addActionListener(event -> updateIngredient());
        removeButton.addActionListener(event -> removeIngredient());
        clearButton.addActionListener(event -> clearIngredientForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(removeButton);
        buttons.add(clearButton);

        GridBagConstraints gbc = baseConstraints(3);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBakedGoodsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout(6, 6));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        searchButton.addActionListener(event -> filterBakedGoods());
        resetButton.addActionListener(event -> {
            searchField.setText("");
            refreshBakedGoodsList();
        });
        searchPanel.add(new JLabel("Name or origin"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        bakedGoodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bakedGoodList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedBakedGood();
            }
        });

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(bakedGoodList), BorderLayout.CENTER);
        panel.add(leftPanel, BorderLayout.WEST);

        JPanel details = new JPanel(new BorderLayout(10, 10));
        details.add(buildBakedGoodForm(), BorderLayout.NORTH);
        details.add(buildRecipePanel(), BorderLayout.CENTER);
        panel.add(details, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildBakedGoodForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Baked good details"));
        addLabelAndField(form, "Name", bakedGoodNameField, 0);
        addLabelAndField(form, "Origin", bakedGoodOriginField, 1);
        addLabelAndField(form, "Image URL", bakedGoodImageField, 2);
        addLabelAndArea(form, "Description", bakedGoodDescriptionArea, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addButton = new JButton("Add baked good");
        JButton updateButton = new JButton("Update selected");
        JButton removeButton = new JButton("Remove selected");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(event -> addBakedGood());
        updateButton.addActionListener(event -> updateBakedGood());
        removeButton.addActionListener(event -> removeBakedGood());
        clearButton.addActionListener(event -> clearBakedGoodForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(removeButton);
        buttons.add(clearButton);

        GridBagConstraints gbc = baseConstraints(4);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);
        return form;
    }

    private JPanel buildRecipePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Recipe"));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        recipeQuantityField.setColumns(8);
        JButton addButton = new JButton("Add to recipe");
        JButton removeButton = new JButton("Remove selected");
        addButton.addActionListener(event -> addRecipeComponent());
        removeButton.addActionListener(event -> removeRecipeComponent());

        controls.add(new JLabel("Ingredient"));
        controls.add(recipeIngredientCombo);
        controls.add(new JLabel("Grams"));
        controls.add(recipeQuantityField);
        controls.add(addButton);
        controls.add(removeButton);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        panel.add(totalCaloriesLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void addIngredient() {
        String name = ingredientNameField.getText().trim();
        String description = ingredientDescriptionArea.getText().trim();
        double calories = parsePositiveDouble(ingredientCaloriesField.getText(), "calories");
        if (name.isEmpty() || calories < 0) return;
        if (ingredientAPI.findByName(name) != null) {
            showError("Ingredient with this name already exists.");
            return;
        }

        ingredientAPI.add(new Ingredient(name, description, calories));
        refreshIngredientsList();
        clearIngredientForm();
    }

    private void updateIngredient() {
        Ingredient ingredient = ingredientList.getSelectedValue();
        if (ingredient == null) {
            showError("Select an ingredient first.");
            return;
        }

        String newName = ingredientNameField.getText().trim();
        double calories = parsePositiveDouble(ingredientCaloriesField.getText(), "calories");
        if (newName.isEmpty() || calories < 0) return;

        String oldName = ingredient.getName();
        Ingredient existing = ingredientAPI.findByName(newName);
        if (existing != null && existing != ingredient) {
            showError("Ingredient with this name already exists.");
            return;
        }

        ingredientAPI.removeByName(oldName);
        ingredient.setName(newName);
        ingredient.setDescription(ingredientDescriptionArea.getText().trim());
        ingredient.setCaloriesPer100g(calories);
        ingredientAPI.add(ingredient);
        refreshAll();
        ingredientList.setSelectedValue(ingredient, true);
    }

    private void removeIngredient() {
        Ingredient ingredient = ingredientList.getSelectedValue();
        if (ingredient == null) {
            showError("Select an ingredient first.");
            return;
        }

        ingredientAPI.removeByName(ingredient.getName());
        clearIngredientForm();
        refreshAll();
    }

    private void addBakedGood() {
        String name = bakedGoodNameField.getText().trim();
        if (name.isEmpty()) {
            showError("Name is required.");
            return;
        }
        if (bakedGoodAPI.findByName(name) != null) {
            showError("Baked good with this name already exists.");
            return;
        }

        BakedGood bakedGood = new BakedGood(
                name,
                bakedGoodOriginField.getText().trim(),
                bakedGoodDescriptionArea.getText().trim(),
                bakedGoodImageField.getText().trim()
        );
        bakedGoodAPI.add(bakedGood);
        refreshBakedGoodsList();
        bakedGoodList.setSelectedValue(bakedGood, true);
    }

    private void updateBakedGood() {
        BakedGood bakedGood = bakedGoodList.getSelectedValue();
        if (bakedGood == null) {
            showError("Select a baked good first.");
            return;
        }

        String newName = bakedGoodNameField.getText().trim();
        if (newName.isEmpty()) {
            showError("Name is required.");
            return;
        }

        String oldName = bakedGood.getName();
        BakedGood existing = bakedGoodAPI.findByName(newName);
        if (existing != null && existing != bakedGood) {
            showError("Baked good with this name already exists.");
            return;
        }

        bakedGoodAPI.removeByName(oldName);
        bakedGood.setName(newName);
        bakedGood.setOrigin(bakedGoodOriginField.getText().trim());
        bakedGood.setDescription(bakedGoodDescriptionArea.getText().trim());
        bakedGood.setImageUrl(bakedGoodImageField.getText().trim());
        bakedGoodAPI.add(bakedGood);
        refreshBakedGoodsList();
        bakedGoodList.setSelectedValue(bakedGood, true);
    }

    private void removeBakedGood() {
        BakedGood bakedGood = bakedGoodList.getSelectedValue();
        if (bakedGood == null) {
            showError("Select a baked good first.");
            return;
        }

        bakedGoodAPI.removeByName(bakedGood.getName());
        clearBakedGoodForm();
        refreshBakedGoodsList();
        refreshRecipeList(null);
    }

    private void addRecipeComponent() {
        BakedGood bakedGood = bakedGoodList.getSelectedValue();
        Ingredient ingredient = (Ingredient) recipeIngredientCombo.getSelectedItem();
        if (bakedGood == null) {
            showError("Select a baked good first.");
            return;
        }
        if (ingredient == null) {
            showError("Add an ingredient first.");
            return;
        }

        double quantity = parsePositiveDouble(recipeQuantityField.getText(), "quantity");
        if (quantity < 0) return;

        recipeAPI.addIngredient(bakedGood, ingredient, quantity);
        recipeQuantityField.setText("");
        refreshRecipeList(bakedGood);
    }

    private void removeRecipeComponent() {
        BakedGood bakedGood = bakedGoodList.getSelectedValue();
        int index = recipeList.getSelectedIndex();
        if (bakedGood == null || index < 0) {
            showError("Select a recipe item first.");
            return;
        }

        recipeAPI.removeIngredient(bakedGood, index);
        refreshRecipeList(bakedGood);
    }

    private void showSelectedIngredient() {
        Ingredient ingredient = ingredientList.getSelectedValue();
        if (ingredient == null) return;

        ingredientNameField.setText(ingredient.getName());
        ingredientCaloriesField.setText(numberFormat.format(ingredient.getCaloriesPer100g()));
        ingredientDescriptionArea.setText(ingredient.getDescription());
    }

    private void showSelectedBakedGood() {
        BakedGood bakedGood = bakedGoodList.getSelectedValue();
        if (bakedGood == null) return;

        bakedGoodNameField.setText(bakedGood.getName());
        bakedGoodOriginField.setText(bakedGood.getOrigin());
        bakedGoodImageField.setText(bakedGood.getImageUrl());
        bakedGoodDescriptionArea.setText(bakedGood.getDescription());
        refreshRecipeList(bakedGood);
    }

    private void refreshAll() {
        refreshIngredientsList();
        refreshBakedGoodsList();
        refreshRecipeList(bakedGoodList.getSelectedValue());
    }

    private void refreshIngredientsList() {
        ingredientListModel.clear();
        ingredientComboModel.removeAllElements();
        for (int i = 0; i < ingredientAPI.ingredients.size(); i++) {
            Ingredient ingredient = ingredientAPI.ingredients.get(i);
            ingredientListModel.addElement(ingredient);
            ingredientComboModel.addElement(ingredient);
        }
    }

    private void refreshBakedGoodsList() {
        bakedGoodListModel.clear();
        for (int i = 0; i < bakedGoodAPI.bakedGoods.size(); i++) {
            bakedGoodListModel.addElement(bakedGoodAPI.bakedGoods.get(i));
        }
    }

    private void refreshRecipeList(BakedGood bakedGood) {
        recipeListModel.clear();
        if (bakedGood == null) {
            totalCaloriesLabel.setText("Total calories: 0");
            return;
        }

        for (int i = 0; i < bakedGood.getRecipe().size(); i++) {
            recipeListModel.addElement(bakedGood.getRecipe().get(i));
        }
        double totalCalories = recipeAPI.calculateTotalCalories(bakedGood);
        totalCaloriesLabel.setText("Total calories: " + numberFormat.format(totalCalories));
    }

    private void filterBakedGoods() {
        String query = searchField.getText().trim().toLowerCase();
        bakedGoodListModel.clear();

        for (int i = 0; i < bakedGoodAPI.bakedGoods.size(); i++) {
            BakedGood bakedGood = bakedGoodAPI.bakedGoods.get(i);
            if (query.isEmpty()
                    || bakedGood.getName().toLowerCase().contains(query)
                    || bakedGood.getOrigin().toLowerCase().contains(query)) {
                bakedGoodListModel.addElement(bakedGood);
            }
        }
    }

    private void clearIngredientForm() {
        ingredientList.clearSelection();
        ingredientNameField.setText("");
        ingredientCaloriesField.setText("");
        ingredientDescriptionArea.setText("");
    }

    private void clearBakedGoodForm() {
        bakedGoodList.clearSelection();
        bakedGoodNameField.setText("");
        bakedGoodOriginField.setText("");
        bakedGoodImageField.setText("");
        bakedGoodDescriptionArea.setText("");
        refreshRecipeList(null);
    }

    private void loadInitialData() {
        if (persistenceManager.dataExists()) {
            loadData();
        } else {
            addSampleData();
        }
    }

    private void loadData() {
        ingredientAPI.clear();
        bakedGoodAPI.clear();

        IngredientAPI loadedIngredients = new IngredientAPI();
        BakedGoodAPI loadedBakedGoods = new BakedGoodAPI();
        PersistenceManager loader = new PersistenceManager(loadedIngredients, loadedBakedGoods, recipeAPI);
        loader.loadAll();

        for (int i = 0; i < loadedIngredients.ingredients.size(); i++) {
            ingredientAPI.add(loadedIngredients.ingredients.get(i));
        }
        for (int i = 0; i < loadedBakedGoods.bakedGoods.size(); i++) {
            bakedGoodAPI.add(loadedBakedGoods.bakedGoods.get(i));
        }
    }

    private void addSampleData() {
        Ingredient flour = new Ingredient("Flour", "Wheat flour", 364);
        Ingredient butter = new Ingredient("Butter", "Dairy butter", 717);
        Ingredient sugar = new Ingredient("Sugar", "White granulated sugar", 387);

        ingredientAPI.add(flour);
        ingredientAPI.add(butter);
        ingredientAPI.add(sugar);

        BakedGood croissant = new BakedGood("Croissant", "France", "Layered pastry", "");
        recipeAPI.addIngredient(croissant, flour, 120);
        recipeAPI.addIngredient(croissant, butter, 80);
        recipeAPI.addIngredient(croissant, sugar, 15);
        bakedGoodAPI.add(croissant);
    }

    private double parsePositiveDouble(String value, String label) {
        try {
            double number = Double.parseDouble(value.trim());
            if (number < 0) {
                showError("The " + label + " value cannot be negative.");
                return -1;
            }
            return number;
        } catch (NumberFormatException e) {
            showError("Enter a valid number for " + label + ".");
            return -1;
        }
    }

    private void addLabelAndField(JPanel panel, String label, JTextField field, int row) {
        GridBagConstraints labelConstraints = baseConstraints(row);
        labelConstraints.gridx = 0;
        labelConstraints.weightx = 0;
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = baseConstraints(row);
        fieldConstraints.gridx = 1;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, fieldConstraints);
    }

    private void addLabelAndArea(JPanel panel, String label, JTextArea area, int row) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        GridBagConstraints labelConstraints = baseConstraints(row);
        labelConstraints.gridx = 0;
        labelConstraints.weightx = 0;
        labelConstraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints areaConstraints = baseConstraints(row);
        areaConstraints.gridx = 1;
        areaConstraints.weightx = 1;
        areaConstraints.weighty = 1;
        areaConstraints.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(area), areaConstraints);
    }

    private GridBagConstraints baseConstraints(int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = row;
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Bakery Store", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Bakery Store", JOptionPane.ERROR_MESSAGE);
    }
}
