package nl.timvandijkhuizen.commerce.gateways;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.PaymentResponse;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Payment;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;

public class GatewayPayPal implements GatewayType {

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
        return Arrays.asList(
            new ConfigOption<>("clientId", "Client Id", Material.NAME_TAG, ConfigTypes.STRING).setRequired(true),
            new ConfigOption<>("clientSecret", "Client Secret", Material.TRIPWIRE_HOOK, ConfigTypes.PASSWORD).setRequired(true),
            new ConfigOption<>("testMode", "Test Mode", Material.COMMAND_BLOCK, ConfigTypes.BOOLEAN)
        );
    }

    @Override
    public String createPaymentUrl(Order order) {
        return null;
    }

    @Override
    public Payment processWebhook(PaymentResponse response) {
        return null;
    }

}
