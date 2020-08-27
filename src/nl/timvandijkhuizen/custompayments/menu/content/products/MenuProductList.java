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
import nl.timvandijkhuizen.custompayments.helpers.PriceHelper;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
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
            item.setName(UI.color(product.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.TEXT_COLOR));
            }
            
            // Category and price
            item.addLore("", UI.color("Category: ", UI.TEXT_COLOR) + UI.color(product.getCategory().getName(), UI.SECONDARY_COLOR));
            item.addLore(UI.color("Price: ", UI.TEXT_COLOR) + UI.color(PriceHelper.format(product.getPrice()), UI.SECONDARY_COLOR), "");
            
            // Commands
            item.addLore(UI.color("Commands:", UI.TEXT_COLOR));
            
            if(product.getCommands().size() > 0) {
                for (Command command : product.getCommands()) {
                    item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command.getCommand(), UI.SECONDARY_COLOR));
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            }

            item.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.CLICK_SOUND);
                
                if (clickType == ClickType.LEFT) {
                    Menus.PRODUCT_EDIT.open(player, product);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.TEXT_COLOR));
                    menu.refresh();

                    productService.deleteProduct(product, success -> {
                        if (success) {
                            UI.playSound(player, UI.DELETE_SOUND);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.ERROR_SOUND);
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
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new product button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Product", UI.SECONDARY_COLOR, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.PRODUCT_EDIT.open(player);
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
