package nl.timvandijkhuizen.custompayments.config.sources;

import java.util.Collection;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.custompayments.base.GatewayType;
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
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> ConfigOption<T> getOption(String path) {
        return getOptions().stream().filter(i -> i.getPath().equals(path)).map(i -> (ConfigOption<T>) i).findFirst().orElse(null);
    }
    
}
