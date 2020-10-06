package nl.timvandijkhuizen.commerce.elements;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.Element;

public class Category extends Element {

    private Material icon;
    private String name;
    private String description;

    public Category() {
        this.icon = Material.CHEST_MINECART;
        this.name = "";
        this.description = "";
    }

    public Category(int id, Material icon, String name, String description) {
        this.setId(id);
        this.icon = icon;
        this.name = name;
        this.description = description;
    }

    @Override
    protected boolean validate(String scenario) {
        if (icon == null) {
            addError("icon", "Icon is required");
            return false;
        }
        
        if (name == null || name.length() == 0) {
            addError("name", "Name is required");
            return false;
        }

        if (name.length() > 40) {
            addError("name", "Name cannot be longer than 40 characters");
            return false;
        }

        if (description == null || description.length() == 0) {
            addError("description", "Description is required");
            return false;
        }

        if (description.length() > 500) {
            addError("description", "Description cannot be longer than 500 characters");
            return false;
        }

        return true;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
