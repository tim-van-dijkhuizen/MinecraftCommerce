package nl.timvandijkhuizen.custompayments.services;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class UserService extends BaseService {

    private Set<ConfigOption<?>> userOptions = new HashSet<>();
    
    @Override
    public String getHandle() {
        return "users";
    }

    @Override
    public void load() throws Exception {
        YamlConfig config = CustomPayments.getInstance().getConfig();
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

    @Override
    public void unload() throws Exception {
        userOptions.clear();
    }
    
    public Set<ConfigOption<?>> getUserOptions() {
        return userOptions;
    }
    
    public UserPreferences getPreferences(Player player) {
        CacheService cacheService = CustomPayments.getInstance().getService("cache");
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
        Storage storage = CustomPayments.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
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
