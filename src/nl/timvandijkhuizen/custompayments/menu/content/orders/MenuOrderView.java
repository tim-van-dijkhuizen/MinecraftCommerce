package nl.timvandijkhuizen.custompayments.menu.content.orders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenOrderList;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
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
        uniqueIdButton.setName(UI.color("Player UniqueId", UI.PRIMARY_COLOR, ChatColor.BOLD));
        uniqueIdButton.addLore(UI.color(order.getPlayerUniqueId().toString(), UI.SECONDARY_COLOR));
        
        menu.setButton(uniqueIdButton, 10);
        
        // Username button
        // ===========================
        MenuItemBuilder usernameButton = new MenuItemBuilder(Material.NAME_TAG);

        usernameButton.setName(UI.color("Player Username", UI.PRIMARY_COLOR, ChatColor.BOLD));
        usernameButton.addLore(UI.color(order.getPlayerName(), UI.SECONDARY_COLOR));
        
        menu.setButton(usernameButton, 12);
        
        // Currency button
        // ===========================
        MenuItemBuilder currencyButton = new MenuItemBuilder(Material.SUNFLOWER);

        currencyButton.setName(UI.color("Currency", UI.PRIMARY_COLOR, ChatColor.BOLD));
        currencyButton.addLore(UI.color(order.getCurrency().getDisplayName(), UI.SECONDARY_COLOR));
        
        menu.setButton(currencyButton, 14);
        
        // Completed button
        // ===========================
        MenuItemBuilder completedButton = new MenuItemBuilder(Material.FIREWORK_ROCKET);

        completedButton.setName(UI.color("Completed", UI.PRIMARY_COLOR, ChatColor.BOLD));
        completedButton.addLore(UI.color(order.isCompleted() ? "Yes" : "No", UI.SECONDARY_COLOR));
        
        menu.setButton(completedButton, 16);
        
        // Products button
        // ===========================
        MenuItemBuilder productsButton = new MenuItemBuilder(Material.CHEST);

        productsButton.setName(UI.color("Products", UI.PRIMARY_COLOR, ChatColor.BOLD));
        productsButton.setLore(UI.color("Click to view the products", UI.TEXT_COLOR));
        
        menu.setButton(productsButton, 30);
        
        // Fields button
        // ===========================
        MenuItemBuilder fieldsButton = new MenuItemBuilder(Material.OAK_SIGN);

        fieldsButton.setName(UI.color("Fields", UI.PRIMARY_COLOR, ChatColor.BOLD));
        fieldsButton.setLore(UI.color("Click to view the fields", UI.TEXT_COLOR));
        
        menu.setButton(fieldsButton, 32);

        // Set bottom line
        // ===========================
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 3);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 5);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 8);

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(new OpenOrderList());

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}
