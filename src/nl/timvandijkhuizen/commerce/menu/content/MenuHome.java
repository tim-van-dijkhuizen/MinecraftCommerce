package nl.timvandijkhuizen.commerce.menu.content;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuHome implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        Menu menu = new Menu("Commerce", MenuSize.XL);

        // Configuration button
        MenuItemBuilder configButton = new MenuItemBuilder(Material.COMPARATOR);

        configButton.setName(UI.color("Configuration", UI.COLOR_PRIMARY, ChatColor.BOLD));
        configButton.setLore(UI.color("Modify your configuration", UI.COLOR_TEXT));

        configButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CONFIG.open(player);
        });

        menu.setButton(configButton, 11);

        // Categories button
        MenuItemBuilder categoriesButton = new MenuItemBuilder(Material.CHEST_MINECART);

        categoriesButton.setName(UI.color("Product Categories", UI.COLOR_PRIMARY, ChatColor.BOLD));
        categoriesButton.setLore(UI.color("Manage product categories", UI.COLOR_TEXT));
        
        categoriesButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CATEGORY_LIST.open(player);
        });

        menu.setButton(categoriesButton, 13);

        // Products button
        MenuItemBuilder productsButton = new MenuItemBuilder(Material.CHEST);

        productsButton.setName(UI.color("Products", UI.COLOR_PRIMARY, ChatColor.BOLD));
        productsButton.setLore(UI.color("Manage products", UI.COLOR_TEXT));
        
        productsButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_LIST.open(player);
        });

        menu.setButton(productsButton, 15);

        // Order fields button
        MenuItemBuilder fieldsButton = new MenuItemBuilder(Material.OAK_SIGN);

        fieldsButton.setName(UI.color("Order fields", UI.COLOR_PRIMARY, ChatColor.BOLD));
        fieldsButton.setLore(UI.color("Manage order fields", UI.COLOR_TEXT));

        fieldsButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_LIST.open(player);
        });

        menu.setButton(fieldsButton, 20);

        // Gateways button
        MenuItemBuilder gatewayButton = new MenuItemBuilder(Material.OAK_FENCE_GATE);

        gatewayButton.setName(UI.color("Gateways", UI.COLOR_PRIMARY, ChatColor.BOLD));
        gatewayButton.setLore(UI.color("Manage gateways", UI.COLOR_TEXT));
        
        gatewayButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_LIST.open(player);
        });

        menu.setButton(gatewayButton, 22);
        
        // Orders button
        MenuItemBuilder orderButton = new MenuItemBuilder(Material.WRITABLE_BOOK);

        orderButton.setName(UI.color("Orders", UI.COLOR_PRIMARY, ChatColor.BOLD));
        orderButton.setLore(UI.color("Manage orders", UI.COLOR_TEXT));
        
        orderButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.ORDER_LIST.open(player);
        });

        menu.setButton(orderButton, 24);

        // Set bottom line
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 3);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 5);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Close button
        MenuItemBuilder closeButton = MenuItems.CLOSE.clone();

        closeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            player.closeInventory();
        });

        menu.setButton(closeButton, 40);

        return menu;
    }

}
