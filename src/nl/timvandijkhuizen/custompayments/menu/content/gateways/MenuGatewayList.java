package nl.timvandijkhuizen.custompayments.menu.content.gateways;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.GatewayConfig;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");
        PagedMenu menu = new PagedMenu("Gateways", 3, 7, 1, 1);

        // Add gateway buttons
        List<Gateway> gateways = args[0].asList(Gateway.class);

        for (Gateway gateway : gateways) {
            MenuItemBuilder item = new MenuItemBuilder(Material.OAK_FENCE_GATE);
            Collection<ConfigOption<?>> options = gateway.getType().getOptions();

            // Set gateway name
            item.setName(UI.color(gateway.getDisplayName(), UI.PRIMARY_COLOR, ChatColor.BOLD));
            item.setLore(UI.color("Type: ", UI.TEXT_COLOR) + UI.color(gateway.getType().getName(), UI.SECONDARY_COLOR), "");
            
            // Add configuration to lore
            item.addLore(UI.color("Configuration:", UI.TEXT_COLOR));
            
            if(options.size() > 0) {
                for(ConfigOption<?> option : options) {
                    GatewayConfig config = gateway.getConfig();
                    ConfigIcon icon = option.getIcon();
                    
                    if(icon != null) {
                        String valueLore = UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC);
                        
                        if(!option.isValueEmpty(config)) {
                            valueLore = UI.color(option.getValueLore(config), UI.SECONDARY_COLOR);
                        }
                        
                        item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.TEXT_COLOR) + valueLore);
                    }
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            }

            item.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.CLICK_SOUND);
                
                if (clickType == ClickType.LEFT) {
                    Menus.GATEWAY_EDIT.open(player, gateway);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.TEXT_COLOR));
                    menu.refresh();

                    gatewayService.deleteGateway(gateway, success -> {
                        if (success) {
                            UI.playSound(player, UI.DELETE_SOUND);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.ERROR_SOUND);
                            item.setLore(UI.color("Error: Failed to delete gateway.", UI.ERROR_COLOR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new gateway button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Gateway", UI.SECONDARY_COLOR, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.GATEWAY_EDIT.open(player);
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
