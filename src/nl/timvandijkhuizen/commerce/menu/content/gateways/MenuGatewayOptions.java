package nl.timvandijkhuizen.commerce.menu.content.gateways;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayOptions implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Gateway Options", 3, 7, 1, 1, 1, 5, 7);
        Gateway gateway = args.get(0);
        GatewayConfig config = gateway.getConfig();

        // Add configuration options
        for (ConfigOption option : config.getOptions()) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());

            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLoreGenerator(() -> {
                List<String> lore = new ArrayList<>();

                if (!option.isValueEmpty(config)) {
                    lore.add(UI.color(option.getValueLore(config), UI.COLOR_SECONDARY));
                } else {
                    lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }

                lore.add("");
                lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                lore.add(UI.color("Right-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

                return lore;
            });

            // Set click listener
            item.setClickListener(event -> {
                ClickType type = event.getClickType();

                if (type == ClickType.LEFT) {
                    UI.playSound(player, UI.SOUND_CLICK);

                    option.getValueInput(config, event, value -> {
                        option.setValue(config, value);
                        menu.open(player);
                    });
                } else if (type == ClickType.RIGHT) {
                    UI.playSound(player, UI.SOUND_DELETE);
                    option.resetValue(config);
                    menu.refresh();
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_EDIT.open(player, gateway);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
