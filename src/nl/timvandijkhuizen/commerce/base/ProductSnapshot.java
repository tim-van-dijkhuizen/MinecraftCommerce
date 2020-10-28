package nl.timvandijkhuizen.commerce.base;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.helpers.DbHelper;

public class ProductSnapshot {

    private Material icon;
    private String name;
    private String description;
    private float price;
    private List<String> commands;

    private Material categoryIcon;
    private String categoryName;
    private String categoryDescription;

    public ProductSnapshot(Product product) {
        icon = product.getIcon();
        name = product.getName();
        description = product.getDescription();
        price = product.getPrice();

        // Set commands
        commands = new ArrayList<>();

        for (Command command : product.getCommands()) {
            commands.add(command.getCommand());
        }

        // Set category
        Category category = product.getCategory();

        categoryIcon = category.getIcon();
        categoryName = category.getName();
        categoryDescription = category.getDescription();
    }

    public ProductSnapshot(JsonObject json) throws JsonParseException {
        try {
            icon = DbHelper.parseMaterial(json.get("icon").getAsString());
            name = json.get("name").getAsString();
            description = json.get("description").getAsString();
            price = json.get("price").getAsFloat();

            // Set commands
            commands = new ArrayList<>();

            for (JsonElement element : json.getAsJsonArray("commands")) {
                commands.add(element.getAsString());
            }

            // Set category
            categoryIcon = DbHelper.parseMaterial(json.get("categoryIcon").getAsString());
            categoryName = json.get("categoryName").getAsString();
            categoryDescription = json.get("categoryDescription").getAsString();
        } catch (Exception e) {
            throw new JsonParseException("Failed to create ProductSnapshopt from JSON: " + e.getMessage());
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("icon", icon.name());
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("price", price);

        // Set commands
        JsonArray commands = new JsonArray();

        for (String command : this.commands) {
            commands.add(command);
        }

        json.add("commands", commands);

        // Set category
        json.addProperty("categoryIcon", categoryIcon.name());
        json.addProperty("categoryName", categoryName);
        json.addProperty("categoryDescription", categoryDescription);

        return json;
    }

    public Material getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public List<String> getCommands() {
        return commands;
    }

    public Material getCategoryIcon() {
        return categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

}
