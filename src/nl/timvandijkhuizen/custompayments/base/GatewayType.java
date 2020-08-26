package nl.timvandijkhuizen.custompayments.base;

import java.util.Collection;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Transaction;
import nl.timvandijkhuizen.custompayments.elements.TransactionResponse;
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
     * Creates a transaction.
     * 
     * @param order
     * @return
     */
    public Transaction createTransaction(Order order);

    /**
     * Returns whether the gateway supports callback's.
     * 
     * @return
     */
    public boolean supportsCallback();

    /**
     * Processes a transaction response.
     * 
     * @param response
     */
    public void processCallback(TransactionResponse response);

}
