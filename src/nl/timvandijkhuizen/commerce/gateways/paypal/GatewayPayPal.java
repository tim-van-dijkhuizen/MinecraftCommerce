package nl.timvandijkhuizen.commerce.gateways.paypal;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;

public class GatewayPayPal implements GatewayType {

	private ConfigOption<String> configClientId;
	private ConfigOption<String> configClientSecret;
	private ConfigOption<Boolean> configTestMode;
	
	public GatewayPayPal() {
		configClientId = new ConfigOption<>("clientId", "Client Id", Material.NAME_TAG, ConfigTypes.STRING).setRequired(true);
		configClientSecret = new ConfigOption<>("clientSecret", "Client Secret", Material.TRIPWIRE_HOOK, ConfigTypes.PASSWORD).setRequired(true);
		configTestMode = new ConfigOption<>("testMode", "Test Mode", Material.COMMAND_BLOCK, ConfigTypes.BOOLEAN);
	}
	
    @Override
    public String getName() {
        return "PayPal";
    }

    @Override
    public String getHandle() {
        return "paypal";
    }
    
    @Override
    public Material getIcon() {
        return Material.LIGHT_BLUE_TERRACOTTA;
    }

    @Override
    public Collection<ConfigOption<?>> getOptions() {
        return Arrays.asList(configClientId, configClientSecret, configTestMode);
    }

	@Override
	public GatewayClient createClient(GatewayConfig config) {
    	String clientId = configClientId.getValue(config);
    	String clientSecret = configClientSecret.getValue(config);
    	boolean testMode = configTestMode.getValue(config);
    	
    	return new ClientPayPal(clientId, clientSecret, testMode);
	}

}
