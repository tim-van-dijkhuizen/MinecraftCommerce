package nl.timvandijkhuizen.custompayments.elements;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.Element;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;

public class Field extends Element {

    private Material icon;
    private String name;
    private String description;
    private ConfigType<?> type;
    private boolean required;
    
    public Field() {
        this.icon = Material.OAK_SIGN;
        this.name = "";
        this.description = "";
    }
    
    public Field(int id, Material icon, String name, String description, ConfigType<?> type, boolean required) {
        this.setId(id);
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
    }
    
    @Override
    public boolean validate() {
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
    
    public ConfigType<?> getType() {
        return type;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public ConfigOption<?> getOption() {
        Integer id = getId();
        
        if(id == null) {
            throw new RuntimeException("Fields must be saved before an option can be created");
        }
        
        return new ConfigOption<>("field-" + id, type);
    }

}
