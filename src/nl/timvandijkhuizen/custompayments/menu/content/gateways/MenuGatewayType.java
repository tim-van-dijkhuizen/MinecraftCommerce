package nl.timvandijkhuizen.custompayments.menu.content.gateways;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.GatewayType;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayType implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Gateway gateway = args[0].as(Gateway.class);
        GatewayType selected = args[1].as(GatewayType.class);
        PagedMenu menu = new PagedMenu("Gateway Type", 3, 7, 1, 1, 1, 5, 7);
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");

        for (GatewayType type : gatewayService.getTypes()) {
            MenuItemBuilder item = new MenuItemBuilder(type.getIcon());

            item.setName(UI.color(type.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            // Enchant if selected
            if (selected != null && type.getHandle().equals(selected.getHandle())) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                gateway.setType(type);
                UI.playSound(player, UI.CLICK_SOUND);
                Menus.GATEWAY_EDIT.open(player, gateway);
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = Menu.CANCEL_BUTTON.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.GATEWAY_EDIT.open(player, gateway);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}