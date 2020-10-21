package nl.timvandijkhuizen.commerce.base;

import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.elements.Order;

public interface OrderEffect {

    String getHandle();

    XMaterial getIcon();

    String getName();

    void playEffect(Player player, Order order);

}
