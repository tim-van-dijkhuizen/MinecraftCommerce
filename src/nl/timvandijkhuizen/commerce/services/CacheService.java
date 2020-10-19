package nl.timvandijkhuizen.commerce.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class CacheService extends BaseService implements Listener {

    private Map<UUID, UserPreferences> userPreferences = new HashMap<>();
    
    @Override
    public String getHandle() {
        return "cache";
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        StorageType storage = Commerce.getInstance().getStorage();
        UUID uuid = event.getUniqueId();
        
        // Load preferences
        try {
            userPreferences.put(uuid, storage.getUserPreferences(uuid));
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load user preferences for " + uuid, e);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        
        userPreferences.remove(uuid);
    }
    
    UserPreferences getPreferences(Player player) {
        return userPreferences.get(player.getUniqueId());
    }
    
    void updatePreferences(Player player, UserPreferences preferences) {
        userPreferences.put(player.getUniqueId(), preferences);
    }

}
