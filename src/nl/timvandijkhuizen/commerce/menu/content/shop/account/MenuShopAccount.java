package nl.timvandijkhuizen.commerce.menu.content.shop.account;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopOrderHistory;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopAccount implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Shop " + Icon.ARROW_RIGHT + " Account", MenuSize.LG);

        // Preferences button
        MenuItemBuilder preferencesButton = new MenuItemBuilder(XMaterial.COMPARATOR);

        preferencesButton.setName(UI.color("My Preferences", UI.COLOR_PRIMARY, ChatColor.BOLD));
        preferencesButton.setLore(UI.color("Manage your preferences", UI.COLOR_TEXT));

        preferencesButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_ACCOUNT_PREFERENCES.open(player);
        });

        menu.setItem(preferencesButton, 11);

        // Account button
        MenuItemBuilder accountButton = new MenuItemBuilder(XMaterial.PLAYER_HEAD);

        accountButton.setSkullOwner(player.getUniqueId());
        accountButton.setName(UI.color("My Account", UI.COLOR_PRIMARY, ChatColor.BOLD));

        menu.setItem(accountButton, 13);

        // Orders button
        MenuItemBuilder ordersButton = new MenuItemBuilder(XMaterial.WRITABLE_BOOK);

        ordersButton.setName(UI.color("My Donations", UI.COLOR_PRIMARY, ChatColor.BOLD));
        ordersButton.setLore(UI.color("View your donation history", UI.COLOR_TEXT));
        ordersButton.setClickListener(new ActionShopOrderHistory());

        menu.setItem(ordersButton, 15);

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

        menu.setItem(closeButton, 31);

        return menu;
    }
    
}
