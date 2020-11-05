package nl.timvandijkhuizen.commerce.config.types;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeTerms implements ConfigType<List<String>> {

    @Override
    public List<String> getValue(OptionConfig config, ConfigOption<List<String>> option) {
        return config.getStringList(option.getPath());
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<List<String>> option, List<String> value) {
        config.set(option.getPath(), value);
    }

    @Override
    public String getRawValue(OptionConfig config, ConfigOption<List<String>> option) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<List<String>> option) {
        return "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<List<String>> option) {
        boolean hasContent = false;
        
        // Check if there are any non-empty pages
        for(String page : getValue(config, option)) {
            if(page.length() > 0) {
                hasContent = true;
            }
        }
        
        return !hasContent;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<List<String>> option, MenuClick event, Consumer<List<String>> callback) {
        Menu menu = new Menu("Place written book", MenuSize.MD);
        Player player = event.getPlayer();

        // Add background items
        MenuItemBuilder gray = MenuItems.BACKGROUND.clone();
        MenuItemBuilder green = new MenuItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE);
        
        menu.setItem(gray, 0);
        menu.setItem(gray, 1);
        menu.setItem(gray, 2);
        menu.setItem(gray, 3);
        menu.setItem(green, 4);
        menu.setItem(green, 5);
        menu.setItem(green, 6);
        menu.setItem(gray, 7);
        menu.setItem(gray, 8);
        
        menu.setItem(gray, 9);
        menu.setItem(gray, 10);
        menu.setItem(gray, 11);
        menu.setItem(gray, 12);
        menu.setItem(green, 13);
        menu.setItem(green, 15);
        menu.setItem(gray, 16);
        menu.setItem(gray, 17);
        
        menu.setItem(gray, 18);
        menu.setItem(gray, 19);
        menu.setItem(gray, 20);
        menu.setItem(gray, 21);
        menu.setItem(green, 22);
        menu.setItem(green, 23);
        menu.setItem(green, 24);
        menu.setItem(gray, 25);
        menu.setItem(gray, 26);
        
        // Add cancel item
        MenuItemBuilder backItem = MenuItems.BACK.clone();

        backItem.setClickListener(backClick -> {
            List<String> value = getValue(config, option);
            
            UI.playSound(player, UI.SOUND_CLICK);
            callback.accept(value);
        });

        menu.setItem(backItem, 11);
        
        // Add menu click listener
        menu.addClickListener(click -> {
            int slot = click.getSlot();
            
            if(slot == 14) {
                InventoryAction action = click.getAction();
                ItemStack item = click.getCursor();
                Material type = item.getType();
                
                if(action == InventoryAction.PLACE_ALL && type == Material.WRITTEN_BOOK) {
                    BookMeta meta = (BookMeta) item.getItemMeta();

                    UI.playSound(player, UI.SOUND_CLICK);
                    click.setCancelled(false);
                    
                    Bukkit.getScheduler().runTaskLater(Commerce.getInstance(), () -> {
                        callback.accept(meta.getPages());
                    }, 1);
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                }
            }
        });
        
        menu.open(player);
    }

}
