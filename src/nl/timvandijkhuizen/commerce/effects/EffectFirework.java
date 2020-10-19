package nl.timvandijkhuizen.commerce.effects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.elements.Order;

public class EffectFirework implements OrderEffect {

	@Override
	public String getHandle() {
		return "firework";
	}

	@Override
	public Material getIcon() {
		return Material.FIREWORK_ROCKET;
	}

	@Override
	public String getName() {
		return "Firework";
	}
	
	@Override
	public void playEffect(Player player, Order order) {
		player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
	}

}
