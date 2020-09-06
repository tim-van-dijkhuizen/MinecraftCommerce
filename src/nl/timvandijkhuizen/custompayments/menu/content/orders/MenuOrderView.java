package nl.timvandijkhuizen.custompayments.menu.content.orders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenOrderList;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
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
        
        menu.setButton(uniqueIdButton, 10);
        
        // Username button
        // ===========================
        MenuItemBuilder usernameButton = new MenuItemBuilder(Material.NAME_TAG);

        usernameButton.setName(UI.color("Player Username", UI.COLOR_PRIMARY, ChatColor.BOLD));
        usernameButton.addLore(UI.color(order.getPlayerName(), UI.COLOR_SECONDARY));
        
        menu.setButton(usernameButton, 12);
        
        // Currency button
        // ===========================
        MenuItemBuilder currencyButton = new MenuItemBuilder(Material.SUNFLOWER);

        currencyButton.setName(UI.color("Currency", UI.COLOR_PRIMARY, ChatColor.BOLD));
        currencyButton.addLore(UI.color(order.getCurrency().getCode(), UI.COLOR_SECONDARY));
        
        menu.setButton(currencyButton, 14);
        
        // Completed button
        // ===========================
        MenuItemBuilder completedButton = new MenuItemBuilder(Material.FIREWORK_ROCKET);

        completedButton.setName(UI.color("Completed", UI.COLOR_PRIMARY, ChatColor.BOLD));
        completedButton.addLore(UI.color(order.isCompleted() ? "Yes" : "No", UI.COLOR_SECONDARY));
        
        menu.setButton(completedButton, 16);
        
        // Products button
        // ===========================
        MenuItemBuilder itemsButton = new MenuItemBuilder(Material.CHEST);

        itemsButton.setName(UI.color("Items", UI.COLOR_PRIMARY, ChatColor.BOLD));
        itemsButton.setLore(UI.color("Click to view the items", UI.COLOR_TEXT));
        
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
