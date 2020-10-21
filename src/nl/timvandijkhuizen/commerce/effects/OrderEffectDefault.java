package nl.timvandijkhuizen.commerce.effects;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.Titles;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OrderEffectDefault implements OrderEffect {

    @Override
    public String getHandle() {
        return "default";
    }

    @Override
    public XMaterial getIcon() {
        return XMaterial.FIREWORK_ROCKET;
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void playEffect(Player player, Order order) {
        OrderService orderServive = Commerce.getInstance().getService("orders");

        // Parse title
        String parsedTitle = orderServive.replaceVariables("Order Completed", order);
        String parsedSubTitle = orderServive.replaceVariables("Thanks for your order {playerUsername}!", order);

        // Color title
        String title = UI.color(parsedTitle, UI.COLOR_PRIMARY, ChatColor.BOLD);
        String subTitle = UI.color(parsedSubTitle, UI.COLOR_TEXT);

        // Show title
        Titles.sendTitle(player, 10, 100, 20, title, subTitle);
        UI.playSound(player, UI.SOUND_SUCCESS);

        // Spawn 5 FireWork rockets at player location with 1 second delay
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
