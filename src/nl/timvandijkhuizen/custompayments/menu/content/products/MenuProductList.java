package nl.timvandijkhuizen.custompayments.menu.content.products;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        ProductService productService = CustomPayments.getInstance().getService("products");
        PagedMenu menu = new PagedMenu("Products", 3, 7, 1, 1);

        // Add product buttons
        List<Product> products = args[0].asList(Product.class);

        for (Product product : products) {
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon());

            // Set product name
            item.setName(UI.color(product.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.TEXT_COLOR));
            }

            item.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                if (clickType == ClickType.LEFT) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    Menus.PRODUCT_EDIT.open(player, product);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.TEXT_COLOR));
                    menu.refresh();

                    productService.deleteProduct(product, success -> {
                        if (success) {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            item.setLore(UI.color("Error: Failed to delete product.", UI.ERROR_COLOR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new product button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Product", UI.SECONDARY_COLOR, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Menus.PRODUCT_EDIT.open(player);
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
