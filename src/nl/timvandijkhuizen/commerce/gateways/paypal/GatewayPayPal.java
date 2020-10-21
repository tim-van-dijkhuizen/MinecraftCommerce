package nl.timvandijkhuizen.commerce.gateways.paypal;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeFile;

public class GatewayPayPal implements GatewayType {

    private ConfigOption<String> configClientId;
    private ConfigOption<String> configClientSecret;
    private ConfigOption<Boolean> configTestMode;
    private ConfigOption<File> configTemplate;

    public GatewayPayPal() {
        File pluginRoot = Commerce.getInstance().getDataFolder();

        configClientId = new ConfigOption<>("clientId", "Client Id", XMaterial.NAME_TAG, ConfigTypes.STRING).setRequired(true);
        configClientSecret = new ConfigOption<>("clientSecret", "Client Secret", XMaterial.TRIPWIRE_HOOK, ConfigTypes.PASSWORD).setRequired(true);
        configTestMode = new ConfigOption<>("testMode", "Test Mode", XMaterial.COMMAND_BLOCK, ConfigTypes.BOOLEAN);
        configTemplate = new ConfigOption<>("template", "Template", XMaterial.MAP, new ConfigTypeFile(pluginRoot, new Pattern[] { Pattern.compile("^.*\\.html$") }));
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
    public XMaterial getIcon() {
        return XMaterial.LIGHT_BLUE_TERRACOTTA;
    }

    @Override
    public Collection<ConfigOption<?>> getOptions() {
        return Arrays.asList(configClientId, configClientSecret, configTestMode, configTemplate);
    }

    @Override
    public GatewayClient createClient(GatewayConfig config) {
        String clientId = configClientId.getValue(config);
        String clientSecret = configClientSecret.getValue(config);
        boolean testMode = configTestMode.getValue(config);
        File template = configTemplate.getValue(config);

        return new ClientPayPal(clientId, clientSecret, testMode, template);
    }

}
