package nl.timvandijkhuizen.commerce.elements;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;

public class Gateway extends Element {

    private String displayName;
    private GatewayType type;
    private GatewayConfig config;
    
    public Gateway() {
        displayName = "";
    }
    
    public Gateway(int id, String displayName, GatewayType type, GatewayConfig config) {
        this.setId(id);
        this.displayName = displayName;
        this.type = type;
        this.config = config;
    }
    
    @Override
    public boolean validate(String scenario) {
        if (displayName == null || displayName.length() == 0) {
            addError("displayName", "Display name is required");
            return false;
        }
        
        if (displayName.length() > 40) {
            addError("displayName", "Display name cannot be longer than 40 characters");
            return false;
        } 
        
        if (type == null) {
            addError("type", "Type is required");
            return false;
        }
        
        if (config == null) {
            addError("config", "Config is required");
            return false;
        }
        
        for(ConfigOption<?> option : type.getOptions()) {
            if(option.isRequired() && option.isValueEmpty(config)) {
                addError("config", "Option \"" + option.getPath() + "\" is required");
                return false;
            }
        }
        
        return true;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public GatewayType getType() {
        return type;
    }
    
    public void setType(GatewayType type) {
        if(this.type != null && this.type.getHandle() != type.getHandle()) {
            this.config = new GatewayConfig(type);
        }
        
        this.type = type;
    }
    
    public GatewayConfig getConfig() {
        return config;
    }

}
