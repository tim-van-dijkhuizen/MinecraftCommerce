package nl.timvandijkhuizen.commerce.menu.content.gateways;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayType implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        PagedMenu menu = new PagedMenu("Gateway Type", 3, 7, 1, 1, 1, 5, 7);
        Gateway gateway = args.get(0);
        GatewayType selected = gateway.getType();

        for (GatewayType type : gatewayService.getTypes()) {
            MenuItemBuilder item = new MenuItemBuilder(type.getIcon());

            item.setName(UI.color(type.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (selected != null && type.getHandle().equals(selected.getHandle())) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                gateway.setType(type);
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.GATEWAY_EDIT.open(player, gateway);
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_EDIT.open(player, gateway);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}