package nl.timvandijkhuizen.commerce.menu.content;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionCategoryList;
import nl.timvandijkhuizen.commerce.menu.actions.ActionFieldList;
import nl.timvandijkhuizen.commerce.menu.actions.ActionGatewayList;
import nl.timvandijkhuizen.commerce.menu.actions.ActionOrderList;
import nl.timvandijkhuizen.commerce.menu.actions.ActionProductList;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuHome implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Commerce", MenuSize.XL);

        // Configuration button
        MenuItemBuilder configButton = new MenuItemBuilder(XMaterial.COMPARATOR);

        configButton.setName(UI.color("Configuration", UI.COLOR_PRIMARY, ChatColor.BOLD));
        configButton.setLore(UI.color("Manage your configuration", UI.COLOR_TEXT));

        configButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CONFIG.open(player);
        });

        menu.setButton(configButton, 11);

        // Categories button
        MenuItemBuilder categoriesButton = new MenuItemBuilder(XMaterial.CHEST_MINECART);

        categoriesButton.setName(UI.color("Product Categories", UI.COLOR_PRIMARY, ChatColor.BOLD));
        categoriesButton.setLore(UI.color("Manage product categories", UI.COLOR_TEXT));
        categoriesButton.setClickListener(new ActionCategoryList());

        menu.setButton(categoriesButton, 13);

        // Products button
        MenuItemBuilder productsButton = new MenuItemBuilder(XMaterial.CHEST);

        productsButton.setName(UI.color("Products", UI.COLOR_PRIMARY, ChatColor.BOLD));
        productsButton.setLore(UI.color("Manage products", UI.COLOR_TEXT));
        productsButton.setClickListener(new ActionProductList());

        menu.setButton(productsButton, 15);

        // Order fields button
        MenuItemBuilder fieldsButton = new MenuItemBuilder(XMaterial.OAK_SIGN);

        fieldsButton.setName(UI.color("Order fields", UI.COLOR_PRIMARY, ChatColor.BOLD));
        fieldsButton.setLore(UI.color("Manage order fields", UI.COLOR_TEXT));
        fieldsButton.setClickListener(new ActionFieldList());

        menu.setButton(fieldsButton, 20);

        // Gateways button
        MenuItemBuilder gatewayButton = new MenuItemBuilder(XMaterial.OAK_FENCE_GATE);

        gatewayButton.setName(UI.color("Gateways", UI.COLOR_PRIMARY, ChatColor.BOLD));
        gatewayButton.setLore(UI.color("Manage gateways", UI.COLOR_TEXT));
        gatewayButton.setClickListener(new ActionGatewayList());

        menu.setButton(gatewayButton, 22);

        // Orders button
        MenuItemBuilder orderButton = new MenuItemBuilder(XMaterial.WRITABLE_BOOK);

        orderButton.setName(UI.color("Orders", UI.COLOR_PRIMARY, ChatColor.BOLD));
        orderButton.setLore(UI.color("Manage orders", UI.COLOR_TEXT));
        orderButton.setClickListener(new ActionOrderList());

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
            menu.close(player);
        });

        menu.setButton(closeButton, 40);

        return menu;
    }

}
