package nl.timvandijkhuizen.custompayments.menu.content;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenCategoryList;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenGatewayList;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenOrderList;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenProductList;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuHome implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Menu menu = new Menu("Custom payments", MenuSize.XL);

        // Configuration button
        MenuItemBuilder configButton = new MenuItemBuilder(Material.COMPARATOR);

        configButton.setName(UI.color("Configuration", UI.PRIMARY_COLOR, ChatColor.BOLD));
        configButton.setLore(UI.color("Modify your configuration", UI.TEXT_COLOR));

        configButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.CONFIG.open(player);
        });

        menu.setButton(configButton, 11);

        // Categories button
        MenuItemBuilder categoriesButton = new MenuItemBuilder(Material.CHEST_MINECART);

        categoriesButton.setName(UI.color("Product Categories", UI.PRIMARY_COLOR, ChatColor.BOLD));
        categoriesButton.setLore(UI.color("Manage product categories", UI.TEXT_COLOR));
        categoriesButton.setClickListener(new OpenCategoryList());

        menu.setButton(categoriesButton, 13);

        // Products button
        MenuItemBuilder productsButton = new MenuItemBuilder(Material.CHEST);

        productsButton.setName(UI.color("Products", UI.PRIMARY_COLOR, ChatColor.BOLD));
        productsButton.setLore(UI.color("Manage products", UI.TEXT_COLOR));
        productsButton.setClickListener(new OpenProductList());

        menu.setButton(productsButton, 15);

        // Order fields button
        MenuItemBuilder fieldsButton = new MenuItemBuilder(Material.OAK_SIGN);

        fieldsButton.setName(UI.color("Order fields", UI.PRIMARY_COLOR, ChatColor.BOLD));
        fieldsButton.setLore(UI.color("Manage order fields", UI.TEXT_COLOR));

        fieldsButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.FIELD_LIST.open(player);
        });

        menu.setButton(fieldsButton, 20);

        // Gateways button
        MenuItemBuilder gatewayButton = new MenuItemBuilder(Material.OAK_FENCE_GATE);

        gatewayButton.setName(UI.color("Gateways", UI.PRIMARY_COLOR, ChatColor.BOLD));
        gatewayButton.setLore(UI.color("Manage gateways", UI.TEXT_COLOR));
        gatewayButton.setClickListener(new OpenGatewayList());

        menu.setButton(gatewayButton, 22);
        
        // Orders button
        MenuItemBuilder orderButton = new MenuItemBuilder(Material.WRITABLE_BOOK);

        orderButton.setName(UI.color("Orders", UI.PRIMARY_COLOR, ChatColor.BOLD));
        orderButton.setLore(UI.color("Manage orders", UI.TEXT_COLOR));
        orderButton.setClickListener(new OpenOrderList());

        menu.setButton(orderButton, 24);

        // Set bottom line
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 3);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 5);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 8);

        // Go back button
        MenuItemBuilder closeButton = Menu.CLOSE_BUTTON.clone();

        closeButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            player.closeInventory();
        });

        menu.setButton(closeButton, 40);

        return menu;
    }

}
