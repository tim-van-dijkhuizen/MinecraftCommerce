package nl.timvandijkhuizen.commerce.base;

import java.util.Collection;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
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
    public XMaterial getIcon();

    /**
     * Returns all configuration options.
     * 
     * @return
     */
    public Collection<ConfigOption<?>> getOptions();

    /**
     * Creates and returns a gateway client for this gateway type.
     * 
     * @return A new GatewayClient
     */
    public GatewayClient createClient(GatewayConfig config);

}
