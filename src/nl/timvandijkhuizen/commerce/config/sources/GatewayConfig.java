package nl.timvandijkhuizen.commerce.config.sources;

import java.util.Collection;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.JsonConfig;

public class GatewayConfig extends JsonConfig {

    private GatewayType gatewayType;
    
    public GatewayConfig(GatewayType gatewayType) {
        this(gatewayType, new JsonObject());
    }
    
    public GatewayConfig(GatewayType gatewayType, JsonObject json) {
        super(json);
        this.gatewayType = gatewayType;
    }
    
    @Override
    public void addOption(ConfigOption<?> option) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addOptions(Collection<ConfigOption<?>> options) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<ConfigOption<?>> getOptions() {
        return gatewayType.getOptions();
    }
    
}
