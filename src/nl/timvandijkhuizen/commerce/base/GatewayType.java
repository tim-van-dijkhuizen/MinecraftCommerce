package nl.timvandijkhuizen.commerce.base;

import java.util.Collection;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Payment;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;

public interface GatewayType {

    /**
     * Returns the type name.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Returns the type handle.
     * 
     * @return
     */
    public String getHandle();

    /**
     * Returns the type icon.
     * 
     * @return
     */
    public Material getIcon();
    
    /**
     * Returns all configuration options.
     * 
     * @return
     */
    public Collection<ConfigOption<?>> getOptions();
    
    /**
     * Creates a payment URL for the specified order.
     * Returns null if we were unable to obtain an url.
     * 
     * @param order
     * @return string|null
     */
    public String createPaymentUrl(Order order);

    /**
     * Processes a webhook response. Returns
     * null if the payment did not succeed.
     * 
     * @param response
     * @return Payment|null
     */
    public Payment processWebhook(PaymentResponse response);

}
