package nl.timvandijkhuizen.commerce.base;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.elements.Order;

public interface OrderEffect {

	String getHandle();
	Material getIcon();
	String getName();
	
	void playEffect(Player player, Order order);
	
}
