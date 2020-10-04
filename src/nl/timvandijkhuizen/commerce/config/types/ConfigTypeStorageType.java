package nl.timvandijkhuizen.commerce.config.types;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeStorageType implements ConfigType<StorageType> {

    private Set<StorageType> availableTypes;
    
    public ConfigTypeStorageType(Set<StorageType> availableTypes) {
        this.availableTypes = availableTypes;
    }
    
    @Override
    public StorageType getValue(OptionConfig config, ConfigOption<StorageType> option) {
        String handle = config.getString(option.getPath());
        
        if(handle == null) {
            return null;
        }
        
        Optional<StorageType> type = availableTypes.stream()
            .filter(i -> i.getHandle().equals(handle))
            .findFirst();

        return type.orElse(null);
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<StorageType> option, StorageType value) {
        config.set(option.getPath(), value.getHandle());
    }

    @Override
    public String getValueLore(OptionConfig config, ConfigOption<StorageType> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getHandle() : "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<StorageType> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<StorageType> option, MenuItemClick event, Consumer<StorageType> callback) {
        PagedMenu menu = new PagedMenu("Select StorageType", 3, 7, 1, 1);
        StorageType selected = getValue(config, option);
        Player player = event.getPlayer();

        for (StorageType type : availableTypes) {
            MenuItemBuilder item = new MenuItemBuilder(Material.BARREL);

            item.setName(UI.color(type.getHandle(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            if(selected != null && selected.getHandle().equals(type.getHandle())) {
                item.addEnchantGlow();
            }
            
            item.setClickListener(itemClick -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(type);
            });

            menu.addPagedButton(item);
        }
        
        menu.open(player);
    }

}
