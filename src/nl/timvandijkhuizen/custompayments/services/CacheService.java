package nl.timvandijkhuizen.custompayments.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class CacheService extends BaseService implements Listener {

    private Map<UUID, UserPreferences> userPreferences = new HashMap<>();
    private Map<UUID, Order> userCarts = new HashMap<>();
    
    @Override
    public String getHandle() {
        return "cache";
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        Storage storage = CustomPayments.getInstance().getStorage();
        UUID uuid = event.getUniqueId();
        
        // Load preferences
        try {
            userPreferences.put(uuid, storage.getUserPreferences(uuid));
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load user preferences for " + uuid, e);
        }
        
        // Load cart
        try {
            userCarts.put(uuid, storage.getCart(uuid));
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load user cart for " + uuid, e);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        
        userPreferences.remove(uuid);
        userCarts.remove(uuid);
    }
    
    UserPreferences getPreferences(Player player) {
        return userPreferences.get(player.getUniqueId());
    }
    
    void updatePreferences(Player player, UserPreferences preferences) {
        userPreferences.put(player.getUniqueId(), preferences);
    }
    
    Order getCart(Player player) {
        return userCarts.get(player.getUniqueId());
    }
    
    void updateCart(Player player, Order cart) {
        userCarts.put(player.getUniqueId(), cart);
    }

}
