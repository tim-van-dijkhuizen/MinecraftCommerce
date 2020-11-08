package nl.timvandijkhuizen.commerce.base;

import java.util.Collection;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;

public interface GatewayType {

    /**
     * Returns the type handle.
     * 
     * @return
     */
    public String getHandle();
    
    /**
     * Returns the type display name.
     * 
     * @return
     */
    public String getDisplayName();

    /**
     * Returns the type icon.
     * 
     * @return
     */
    public XMaterial getIcon();

    /**
     * Returns all configuration options.
     * 
     * @return
     */
    public Collection<ConfigOption<?>> getOptions();

    /**
     * Creates a GatewayClient for this GatewayType.
     * 
     * @param gateway
     * @return A new GatewayClient
     */
    public GatewayClient createClient(Gateway gateway);

}
