package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopGateway implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        PagedMenu menu = new PagedMenu("Cart " + Icon.ARROW_RIGHT + " Gateways", 3, 7, 1, 1, 2, 5, 6);
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        Set<Gateway> gateways = gatewayService.getGateways();
        
        // Add fields
        for (Gateway gateway : gateways) {
            MenuItemBuilder item = new MenuItemBuilder(gateway.getType().getIcon());
            
            item.setName(UI.color(gateway.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.addLore("", UI.color("Left-click to pay using this gateway.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
            });
            
            menu.addPagedButton(item);
        }
        
        // Previous (cart) button
        MenuItemBuilder previousButton = new MenuItemBuilder(Material.MINECART);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Fields", UI.COLOR_TEXT));
        
        previousButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_FIELDS.open(player);
        });

        menu.setButton(previousButton, menu.getSize().getSlots() - 9);
        
        // Cart button
        menu.setButton(ShopHelper.createCartItem(player), menu.getSize().getSlots() - 9 + 3);
        
        return menu;
    }

}
