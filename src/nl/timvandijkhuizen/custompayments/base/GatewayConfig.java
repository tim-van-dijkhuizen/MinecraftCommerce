package nl.timvandijkhuizen.custompayments.base;

import java.util.Collection;
import java.util.Map.Entry;

import org.bukkit.configuration.MemoryConfiguration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;

public class GatewayConfig extends MemoryConfiguration implements OptionConfig {

    private GatewayType gatewayType;
    
    public GatewayConfig(GatewayType gatewayType) {
        this.gatewayType = gatewayType;
    }
    
    public GatewayConfig(GatewayType gatewayType, JsonObject json) {
        this.gatewayType = gatewayType;
        
        // Load config from json
        for(Entry<String, JsonElement> entry : json.entrySet()) {
            set(entry.getKey(), entry.getValue().getAsString());
        }
    }
    
    @Override
    public void addOption(ConfigOption<?> option) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<ConfigOption<?>> getOptions() {
        return gatewayType.getOptions();
    }
    
    @Override
    public ConfigOption<?> getOption(String path) {
        return getOptions().stream().filter(i -> i.getPath().equals(path)).findFirst().orElse(null);
    }
    
}
