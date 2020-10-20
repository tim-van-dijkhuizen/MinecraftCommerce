package nl.timvandijkhuizen.commerce.effects;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.elements.Order;

public class EffectFirework implements OrderEffect {

	@Override
	public String getHandle() {
		return "firework";
	}

	@Override
	public XMaterial getIcon() {
		return XMaterial.FIREWORK_ROCKET;
	}

	@Override
	public String getName() {
		return "Firework";
	}
	
	@Override
	public void playEffect(Player player, Order order) {
        new BukkitRunnable() {
			int count = 0;
			  
			public void run() {
				if (count <= 4) {
					player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
					count += 1;
				} else {
					cancel();
				}
			}
        }.runTaskTimer(Commerce.getInstance(), 0, 20);
	}

}
