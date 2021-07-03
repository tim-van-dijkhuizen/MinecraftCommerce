package nl.timvandijkhuizen.commerce.menu.content.products;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.helpers.ValidationHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionProductList;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.InputHelper;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        ProductService productService = Commerce.getInstance().getService(ProductService.class);
        Product product = args.get(0, new Product());
        Menu menu = new Menu("Admin " + Icon.ARROW_RIGHT + " " + (product.getId() != null ? "Edit" : "Create") + " Product", MenuSize.XXL);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder();

        iconButton.setTypeGenerator(() -> product.getIcon());
        iconButton.setName(UI.color("Icon", UI.COLOR_PRIMARY, ChatColor.BOLD));

        iconButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            ValidationHelper.addErrorLore(lore, product, "icon");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_ICON.open(player, product, menu);
        });

        menu.setItem(iconButton, 11);

        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(XMaterial.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        nameButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (product.getName().length() > 0) {
                lore.add(UI.color(product.getName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, product, "name");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        nameButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);

            InputHelper.getString(player, UI.color("What should be the name of the product?", UI.COLOR_PRIMARY), (ctx, input) -> {
                product.setName(input);
                menu.open(player);
                return null;
            });
        });

        menu.setItem(nameButton, 13);

        // Description button
        // ===========================
        MenuItemBuilder descriptionButton = new MenuItemBuilder(XMaterial.PAPER);

        descriptionButton.setName(UI.color("Description", UI.COLOR_PRIMARY, ChatColor.BOLD));

        descriptionButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (product.getDescription().length() > 0) {
                String[] lines = ShopHelper.parseDescription(product.getDescription());

                for (String line : lines) {
                    lore.add(UI.color(line, UI.COLOR_TEXT));
                }
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, product, "description");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit using chat.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            lore.add(UI.color("Right-click to edit using a book.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        descriptionButton.setClickListener(event -> {
            ClickType type = event.getClickType();
            
            UI.playSound(player, UI.SOUND_CLICK);
            
            if(type == ClickType.LEFT) {
                menu.close(player);
                
                InputHelper.getString(player, UI.color("What should be the description of the product?", UI.COLOR_PRIMARY), (ctx, input) -> {
                    product.setDescription(input);
                    menu.open(player);
                    return null;
                });
            } else if(type == ClickType.RIGHT) {
                InputHelper.getText(player, input -> {
                    if(input != null) {
                        product.setDescription(String.join("\n", input));
                    }
                    
                    menu.open(player);
                });
            }
        });

        menu.setItem(descriptionButton, 15);

        // Category button
        // ===========================
        MenuItemBuilder categoryButton = new MenuItemBuilder(XMaterial.CHEST_MINECART);

        categoryButton.setName(UI.color("Category", UI.COLOR_PRIMARY, ChatColor.BOLD));

        categoryButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (product.getCategory() != null) {
                lore.add(UI.color(product.getCategory().getName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, product, "category");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        categoryButton.setClickListener(event -> {
            CategoryService categoryService = Commerce.getInstance().getService(CategoryService.class);

            UI.playSound(player, UI.SOUND_CLICK);
            categoryButton.setLore(UI.color("Loading...", UI.COLOR_TEXT));
            menu.disableItems();
            menu.refresh();

            // Create menu
            categoryService.getCategories(categories -> {
                menu.enableItems();

                if (categories == null) {
                    UI.playSound(player, UI.SOUND_ERROR);
                    categoryButton.setLore(UI.color("Error: Failed to load categories.", UI.COLOR_ERROR));
                    menu.refresh();
                    return;
                }

                Menus.PRODUCT_CATEGORY.open(player, product, categories, menu);
            });
        });

        menu.setItem(categoryButton, 29);

        // Price button
        // ===========================
        MenuItemBuilder priceButton = new MenuItemBuilder(XMaterial.SUNFLOWER);

        priceButton.setName(UI.color("Price", UI.COLOR_PRIMARY, ChatColor.BOLD));

        priceButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            lore.add(UI.color(ShopHelper.formatPrice(product.getPrice()), UI.COLOR_TEXT));

            ValidationHelper.addErrorLore(lore, product, "price");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        priceButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);

            InputHelper.getNumber(player, UI.color("What should be the price of the product?", UI.COLOR_PRIMARY), (ctx, input) -> {
                product.setPrice(input.floatValue());
                menu.open(player);
                return null;
            });
        });

        menu.setItem(priceButton, 31);

        // Commands button
        // ===========================
        MenuItemBuilder commandsButton = new MenuItemBuilder(XMaterial.COMMAND_BLOCK);

        commandsButton.setName(UI.color("Commands", UI.COLOR_PRIMARY, ChatColor.BOLD));

        commandsButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (product.getCommands().size() > 0) {
                for (Command command : product.getCommands()) {
                    lore.add(UI.color(Icon.SQUARE + " " + command.getCommand(), UI.COLOR_TEXT));
                }
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        commandsButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_COMMANDS.open(player, product, menu);
        });

        menu.setItem(commandsButton, 33);

        // Set bottom line
        // ===========================
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(new ActionProductList());

        menu.setItem(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableItems();
            menu.refresh();

            // Save product
            productService.saveProduct(product, success -> {
                menu.enableItems();

                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    saveButton.setLore("");
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    saveButton.setLore(UI.color("Error: Field contains an invalid value.", UI.COLOR_ERROR));
                }
                
                menu.refresh();
            });
        });

        menu.setItem(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
