package nl.timvandijkhuizen.commerce.elements.editable;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.elements.Category;

public class EditableCategory extends Category {

    public EditableCategory() {
        super();
    }
    
    public EditableCategory(Category source) {
        super(source.getId(), source.getIcon(), source.getName(), source.getDescription());
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
