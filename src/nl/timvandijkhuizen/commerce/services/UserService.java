package nl.timvandijkhuizen.commerce.services;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class UserService extends BaseService {

    private Set<ConfigOption<?>> userOptions = new LinkedHashSet<>();
    
    @Override
    public String getHandle() {
        return "users";
    }

    @Override
    public void init() throws Exception {
        YamlConfig config = Commerce.getInstance().getConfig();
        ConfigOption<StoreCurrency> optionBaseCurrency = config.getOption("general.baseCurrency");
        StoreCurrency baseCurrency = optionBaseCurrency.getValue(config);
        
        // Create user options
        ConfigOption<StoreCurrency> optionCurrency = new ConfigOption<>("currency", new ConfigTypeStoreCurrency())
            .setIcon(new ConfigIcon(Material.SUNFLOWER, "Currency"))
            .setRequired(true)
            .setDefaultValue(baseCurrency);
        
        // Register user options
        userOptions.add(optionCurrency);
    }
    
    public Set<ConfigOption<?>> getUserOptions() {
        return userOptions;
    }
    
    public UserPreferences getPreferences(Player player) {
        CacheService cacheService = Commerce.getInstance().getService("cache");
        UserPreferences preferences = cacheService.getPreferences(player);
        
        // Set default if null
        if(preferences == null) {
            preferences = new UserPreferences();
            cacheService.updatePreferences(player, preferences);
            return preferences;
        }
        
        return preferences;
    }
    
    public void savePreferences(Player player, UserPreferences preferences, Consumer<Boolean> callback) {
        Storage storage = Commerce.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                storage.saveUserPreferences(player.getUniqueId(), preferences);
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to save user preferences: " + e.getMessage(), e);
            }
        });
    }

}
