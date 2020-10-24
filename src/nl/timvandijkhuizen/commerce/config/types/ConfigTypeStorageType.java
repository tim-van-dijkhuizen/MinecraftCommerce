package nl.timvandijkhuizen.commerce.config.types;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeStorageType implements ConfigType<StorageType> {

    @Override
    public StorageType getValue(OptionConfig config, ConfigOption<StorageType> option) {
        Set<StorageType> types = Commerce.getInstance().getStorageTypes();
        String handle = config.getString(option.getPath());

        if (handle == null) {
            return null;
        }

        Optional<StorageType> type = types.stream()
            .filter(i -> i.getType().equals(handle))
            .findFirst();

        return type.orElse(null);
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<StorageType> option, StorageType value) {
        config.set(option.getPath(), value != null ? value.getType() : null);
    }

    @Override
    public String getValueLore(OptionConfig config, ConfigOption<StorageType> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getType() : "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<StorageType> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<StorageType> option, MenuItemClick event, Consumer<StorageType> callback) {
        PagedMenu menu = new PagedMenu("Choose a storage type", 3, 7, 1, 1, 1, 5, 7);
        Player player = event.getPlayer();
        StorageType selected = getValue(config, option);

        // Add available types
        Set<StorageType> types = Commerce.getInstance().getStorageTypes();
        
        for (StorageType type : types) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.BOOKSHELF);

            item.setName(UI.color(type.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            if (selected != null && type.getType().equals(selected.getType())) {
                item.addEnchantGlow();
            }

            item.setClickListener(itemClick -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(type);
            });

            menu.addPagedButton(item);
        }
        
        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(backEvent -> {
            UI.playSound(player, UI.SOUND_CLICK);
            callback.accept(selected);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        menu.open(player);
    }

}
