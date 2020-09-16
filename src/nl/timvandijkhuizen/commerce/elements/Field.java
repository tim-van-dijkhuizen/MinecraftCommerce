package nl.timvandijkhuizen.commerce.elements;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;

public class Field extends Element {

    private Material icon;
    private String name;
    private String description;
    private FieldType<?> type;
    private boolean required;
    
    public Field() {
        this.icon = Material.OAK_SIGN;
        this.name = "";
        this.description = "";
    }
    
    public Field(int id, Material icon, String name, String description, FieldType<?> type, boolean required) {
        this.setId(id);
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
    }
    
    @Override
    public boolean validate(String scenario) {
        if (icon == null) {
            addError("icon", "Icon is required");
            return false;
        }

        if (name.length() == 0) {
            addError("name", "Name is required");
            return false;
        }

        if (name.length() > 40) {
            addError("name", "Name cannot be longer than 40 characters");
            return false;
        }

        if (description.length() == 0) {
            addError("description", "Description is required");
            return false;
        }

        if (description.length() > 500) {
            addError("description", "Description cannot be longer than 500 characters");
            return false;
        }
        
        if (type == null) {
            addError("type", "Type is required");
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
    
    public FieldType<?> getType() {
        return type;
    }
    
    public void setType(FieldType<?> type) {
        this.type = type;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public ConfigOption<?> getOption() {
        Integer id = getId();
        
        if(id == null) {
            throw new RuntimeException("Fields must be saved before an option can be created");
        }
        
        return new ConfigOption<>("field-" + id, name, icon, type)
            .setRequired(required);
    }

}
