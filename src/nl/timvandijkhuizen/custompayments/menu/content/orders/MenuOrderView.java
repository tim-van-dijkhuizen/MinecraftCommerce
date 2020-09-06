package nl.timvandijkhuizen.custompayments.menu.content.orders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.base.ProductSnapshot;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.helpers.ShopHelper;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenOrderList;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderView implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Order order = args[0].as(Order.class);
        Menu menu = new Menu("View Order", MenuSize.XXL);

        // UniqueId button
        // ===========================
        MenuItemBuilder uniqueIdButton = new MenuItemBuilder(Material.PLAYER_HEAD);

        uniqueIdButton.setSkullOwner(order.getPlayerUniqueId());
        uniqueIdButton.setName(UI.color("Player UniqueId", UI.COLOR_PRIMARY, ChatColor.BOLD));
        uniqueIdButton.addLore(UI.color(order.getPlayerUniqueId().toString(), UI.COLOR_SECONDARY));
        
        menu.setButton(uniqueIdButton, 11);
        
        // Username button
        // ===========================
        MenuItemBuilder usernameButton = new MenuItemBuilder(Material.NAME_TAG);

        usernameButton.setName(UI.color("Player Username", UI.COLOR_PRIMARY, ChatColor.BOLD));
        usernameButton.addLore(UI.color(order.getPlayerName(), UI.COLOR_SECONDARY));
        
        menu.setButton(usernameButton, 13);
        
        // Currency button
        // ===========================
        MenuItemBuilder currencyButton = new MenuItemBuilder(Material.SUNFLOWER);

        currencyButton.setName(UI.color("Currency", UI.COLOR_PRIMARY, ChatColor.BOLD));
        currencyButton.addLore(UI.color(order.getCurrency().getCode(), UI.COLOR_SECONDARY));
        
        menu.setButton(currencyButton, 15);
        
        // Products button
        // ===========================
        MenuItemBuilder itemsButton = new MenuItemBuilder(Material.CHEST);

        itemsButton.setName(UI.color("Items", UI.COLOR_PRIMARY, ChatColor.BOLD));
        
        // Add items to lore
        DataList<LineItem> lineItems = order.getLineItems();

        if (lineItems.size() > 0) {
            for(LineItem lineItem : lineItems) {
                ProductSnapshot product = lineItem.getProduct();
                String quantity = lineItem.getQuantity() > 1 ? (lineItem.getQuantity() + "x ") : "";
                String price = ShopHelper.formatPrice(product.getPrice(), order.getCurrency());
                
                itemsButton.addLore(UI.TAB + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + " " + UI.color(quantity + product.getName() + " " + Icon.ARROW_RIGHT + " " + price, UI.COLOR_SECONDARY));
            }
        } else {
            itemsButton.addLore(UI.TAB + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        itemsButton.addLore("", UI.color("Click to view details.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        
        itemsButton.setClickListener(event -> {
           UI.playSound(player, UI.SOUND_CLICK);
           Menus.ORDER_ITEMS.open(player, order);
        });
        
        menu.setButton(itemsButton, 30);
        
        // Fields button
        // ===========================
        MenuItemBuilder fieldsButton = new MenuItemBuilder(Material.OAK_SIGN);

        fieldsButton.setName(UI.color("Fields", UI.COLOR_PRIMARY, ChatColor.BOLD));
        fieldsButton.setLore(UI.color("Click to view the fields", UI.COLOR_TEXT));
        
        menu.setButton(fieldsButton, 32);

        // Set bottom line
        // ===========================
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 3);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 5);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(new OpenOrderList());

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}
