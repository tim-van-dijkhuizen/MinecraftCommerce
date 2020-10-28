package nl.timvandijkhuizen.commerce.services;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class UserService extends BaseService {

    private Set<ConfigOption<?>> userOptions = new LinkedHashSet<>();

    @Override
    public String getHandle() {
        return "users";
    }

    @Override
    public void init() throws Throwable {
        YamlConfig config = Commerce.getInstance().getConfig();
        ConfigOption<StoreCurrency> optionBaseCurrency = config.getOption("general.baseCurrency");
        StoreCurrency baseCurrency = optionBaseCurrency.getValue(config);

        // Create user options
        ConfigOption<StoreCurrency> optionCurrency = new ConfigOption<>("currency", "Currency", XMaterial.SUNFLOWER, new ConfigTypeStoreCurrency(false))
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
        if (preferences == null) {
            return new UserPreferences();
        }

        return preferences;
    }

    public void savePreferences(Player player, UserPreferences preferences, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.executeAsync(() -> {
            storage.saveUserPreferences(player.getUniqueId(), preferences);
        }, () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to save user preferences: " + error.getMessage(), error);
        });
    }

}
