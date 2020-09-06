package nl.timvandijkhuizen.custompayments.menu.content.products;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.ShopHelper;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
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
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }
            
            // Category and price
            item.addLore("", UI.color("Category: ", UI.COLOR_TEXT) + UI.color(product.getCategory().getName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Price: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(product.getPrice()), UI.COLOR_SECONDARY), "");
            
            // Commands
            item.addLore(UI.color("Commands:", UI.COLOR_TEXT));
            
            if(product.getCommands().size() > 0) {
                for (Command command : product.getCommands()) {
                    item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command.getCommand(), UI.COLOR_SECONDARY));
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            item.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.SOUND_CLICK);
                
                if (clickType == ClickType.LEFT) {
                    Menus.PRODUCT_EDIT.open(player, product);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.COLOR_TEXT));
                    menu.refresh();

                    productService.deleteProduct(product, success -> {
                        if (success) {
                            UI.playSound(player, UI.SOUND_DELETE);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete product.", UI.COLOR_ERROR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new product button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Product", UI.COLOR_SECONDARY, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_EDIT.open(player);
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
