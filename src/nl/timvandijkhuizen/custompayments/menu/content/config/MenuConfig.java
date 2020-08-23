package nl.timvandijkhuizen.custompayments.menu.content.config;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuConfig implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        PagedMenu menu = new PagedMenu("Config", 3, 7, 1, 1, 1, 5, 7);

        // Add config options
        for (ConfigOption option : config.getOptions()) {
            ConfigType type = option.getType();
            
            // Add option if it has an icon
            if(option.getIcon() != null) {
                menu.addPagedButton(type.createMenuItem(config, option));
            }
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
