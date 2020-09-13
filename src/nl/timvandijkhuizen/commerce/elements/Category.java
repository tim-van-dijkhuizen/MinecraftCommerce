package nl.timvandijkhuizen.commerce.elements;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.base.SourceElement;
import nl.timvandijkhuizen.commerce.elements.editable.EditableCategory;

public class Category extends Element implements SourceElement<EditableCategory> {

    protected Material icon;
    protected String name;
    protected String description;

    public Category(Material icon, String name, String description) {
        this.icon = icon;
        this.name = name;
        this.description = description;
    }
    
    public Category(int id, Material icon, String name, String description) {
        this.setId(id);
        this.icon = icon;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean validate(String scenario) {
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
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public EditableCategory getEditableCopy() {
        return new EditableCategory(getId(), icon, name, description);
    }

    @Override
    public void updateFromCopy(EditableCategory copy) {
        this.icon = copy.getIcon();
        this.name = copy.getName();
        this.description = copy.getDescription();
    }

}
