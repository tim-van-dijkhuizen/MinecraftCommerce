package nl.timvandijkhuizen.commerce.base;

import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.elements.Order;

public interface OrderEffect {

    /**
     * Returns the handle.
     * 
     * @return
     */
    String getHandle();

    /**
     * Returns the display name.
     * 
     * @return
     */
    String getDisplayName();
    
    /**
     * Returns the icon.
     * 
     * @return
     */
    XMaterial getIcon();

    /**
     * Plays the effect for an order. 
     * 
     * @param player
     * @param order
     */
    void playEffect(Player player, Order order);

}
