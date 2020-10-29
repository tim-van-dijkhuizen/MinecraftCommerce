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
    public String getDisplayName() {
        return "Default";
    }

    @Override
    public void playEffect(Player player, Order order) {
        String title = UI.color("Donation Completed", UI.COLOR_PRIMARY, ChatColor.BOLD);
        String subTitle = UI.color("Thanks for your donation " + order.getPlayerName(), UI.COLOR_TEXT);

        Titles.sendTitle(player, 10, 100, 20, title, subTitle);
        UI.playSound(player, UI.SOUND_SUCCESS);

        // Send chat message
        player.sendMessage(UI.color(UI.LINE, UI.COLOR_TEXT, ChatColor.BOLD));
        player.sendMessage("");
        player.sendMessage(UI.color("Thank you for your donation! We’ve successfully added the items to your account.", UI.COLOR_PRIMARY));
        player.sendMessage(UI.color("You can view your donation history using ", UI.COLOR_TEXT) + UI.color("/shop account", UI.COLOR_SECONDARY) + UI.color(".", UI.COLOR_TEXT));
        player.sendMessage("");
        player.sendMessage(UI.color(UI.LINE, UI.COLOR_TEXT, ChatColor.BOLD));
        
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
