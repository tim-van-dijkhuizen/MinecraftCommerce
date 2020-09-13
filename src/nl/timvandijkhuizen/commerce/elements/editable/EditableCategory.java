package nl.timvandijkhuizen.commerce.elements.editable;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.elements.Category;

public class EditableCategory extends Category {

    public EditableCategory() {
        super(Material.CHEST_MINECART, "", "");
    }
    
    public EditableCategory(int id, Material icon, String name, String description) {
        super(id, icon, name, description);
    }
    
    public void setIcon(Material icon) {
        this.icon = icon;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
