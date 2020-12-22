package nl.timvandijkhuizen.commerce.config.types;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.services.StorageService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeStorageType implements ConfigType<StorageType> {

    @Override
    public StorageType getValue(OptionConfig config, ConfigOption<StorageType> option) {
        StorageService storageService = Commerce.getInstance().getService("storage");
        Set<StorageType> types = storageService.getStorageTypes();
        
        // Get handle
        String handle = config.getString(option.getPath());

        if (handle == null) {
            return getFallbackType();
        }

        // Find type with that handle
        Optional<StorageType> type = types.stream()
            .filter(i -> i.getHandle().equals(handle))
            .findFirst();

        return type.orElse(getFallbackType());
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<StorageType> option, StorageType value) {
        config.set(option.getPath(), value != null ? value.getHandle() : null);
    }

    @Override
    public String getRawValue(OptionConfig config, ConfigOption<StorageType> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getHandle() : "";
    }
    
    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<StorageType> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getDisplayName() : "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<StorageType> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<StorageType> option, MenuClick event, Consumer<StorageType> callback) {
        PagedMenu menu = new PagedMenu("Choose a storage type", 3, 7, 1, 1, 1, 5, 7);
        Player player = event.getPlayer();
        StorageType selected = getValue(config, option);

        // Add available types
        StorageService storageService = Commerce.getInstance().getService("storage");
        Set<StorageType> types = storageService.getStorageTypes();
        
        for (StorageType type : types) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.BOOKSHELF);

            item.setName(UI.color(type.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            if (selected != null && type.getHandle().equals(selected.getHandle())) {
                item.addEnchantGlow();
            }

            item.setClickListener(itemClick -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(type);
            });

            menu.addPagedItem(item);
        }
        
        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(backEvent -> {
            UI.playSound(player, UI.SOUND_CLICK);
            callback.accept(selected);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        menu.open(player);
    }
    
    private StorageType getFallbackType() {
        StorageService storageService = Commerce.getInstance().getService("storage");
        Set<StorageType> storageTypes = storageService.getStorageTypes();
        
        // Get first type
        Optional<StorageType> fallback = storageTypes
            .stream()
            .findFirst();

        return fallback.orElse(null);
    }

}
