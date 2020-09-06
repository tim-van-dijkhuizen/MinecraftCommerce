package nl.timvandijkhuizen.custompayments.menu.content.products;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductIcon implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Product product = args[0].as(Product.class);
        Material selected = args[1].as(Material.class);
        PagedMenu menu = new PagedMenu("Product Icon", 3, 7, 1, 1, 1, 5, 7);

        for (Material icon : CustomPayments.MENU_ICONS) {
            MenuItemBuilder item = new MenuItemBuilder(icon);

            item.setName(UI.color(WordUtils.capitalize(icon.name().replace('_', ' ').toLowerCase()), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (icon == selected) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                product.setIcon(icon);
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.PRODUCT_EDIT.open(player, product);
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_EDIT.open(player, product);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}