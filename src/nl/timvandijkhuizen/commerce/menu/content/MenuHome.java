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
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuHome implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Admin " + Icon.ARROW_RIGHT + " Home", MenuSize.XL);

        // Configuration button
        MenuItemBuilder configButton = new MenuItemBuilder(XMaterial.COMPARATOR);

        configButton.setName(UI.color("Configuration", UI.COLOR_PRIMARY, ChatColor.BOLD));
        configButton.setLore(UI.color("Manage your configuration.", UI.COLOR_TEXT));

        configButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CONFIG.open(player);
        });

        menu.setItem(configButton, 11);

        // Categories button
        MenuItemBuilder categoriesButton = new MenuItemBuilder(XMaterial.CHEST_MINECART);

        categoriesButton.setName(UI.color("Categories", UI.COLOR_PRIMARY, ChatColor.BOLD));
        categoriesButton.setLore(UI.color("Manage your categories.", UI.COLOR_TEXT));
        categoriesButton.setClickListener(new ActionCategoryList());

        menu.setItem(categoriesButton, 13);

        // Products button
        MenuItemBuilder productsButton = new MenuItemBuilder(XMaterial.CHEST);

        productsButton.setName(UI.color("Products", UI.COLOR_PRIMARY, ChatColor.BOLD));
        productsButton.setLore(UI.color("Manage your products.", UI.COLOR_TEXT));
        productsButton.setClickListener(new ActionProductList());

        menu.setItem(productsButton, 15);

        // Order fields button
        MenuItemBuilder fieldsButton = new MenuItemBuilder(XMaterial.OAK_SIGN);

        fieldsButton.setName(UI.color("Fields", UI.COLOR_PRIMARY, ChatColor.BOLD));
        fieldsButton.setLore(UI.color("Manage your fields.", UI.COLOR_TEXT));
        fieldsButton.setClickListener(new ActionFieldList());

        menu.setItem(fieldsButton, 20);

        // Gateways button
        MenuItemBuilder gatewayButton = new MenuItemBuilder(XMaterial.OAK_FENCE_GATE);

        gatewayButton.setName(UI.color("Gateways", UI.COLOR_PRIMARY, ChatColor.BOLD));
        gatewayButton.setLore(UI.color("Manage your gateways.", UI.COLOR_TEXT));
        gatewayButton.setClickListener(new ActionGatewayList());

        menu.setItem(gatewayButton, 22);

        // Orders button
        MenuItemBuilder orderButton = new MenuItemBuilder(XMaterial.WRITABLE_BOOK);

        orderButton.setName(UI.color("Orders", UI.COLOR_PRIMARY, ChatColor.BOLD));
        orderButton.setLore(UI.color("Manage your orders.", UI.COLOR_TEXT));
        orderButton.setClickListener(new ActionOrderList());

        menu.setItem(orderButton, 24);

        // Set bottom line
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 3);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 5);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Close button
        MenuItemBuilder closeButton = MenuItems.CLOSE.clone();

        closeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);
        });

        menu.setItem(closeButton, 40);

        return menu;
    }

}
