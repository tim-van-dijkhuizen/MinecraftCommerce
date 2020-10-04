package nl.timvandijkhuizen.commerce.menu.content.products;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionProductList;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
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
        ProductService productService = Commerce.getInstance().getService("products");
        Product product = args.get(0, new Product());
        Menu menu = new Menu((product.getId() != null ? "Edit" : "Create") + " Product", MenuSize.XXL);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder(product.getIcon());

        iconButton.setName(UI.color("Icon", UI.COLOR_PRIMARY, ChatColor.BOLD));

        // Add validation errors to lore
        if (product.hasErrors("icon")) {
            iconButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            iconButton.addEnchantGlow();

            for (String error : product.getErrors("icon")) {
                iconButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }
        
        iconButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_ICON.open(player, product);
        });

        menu.setButton(iconButton, 11);

        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(Material.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (product.getName().length() > 0) {
            nameButton.addLore(UI.color(product.getName(), UI.COLOR_SECONDARY));
        } else {
            nameButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        nameButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (product.hasErrors("name")) {
            nameButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            nameButton.addEnchantGlow();

            for (String error : product.getErrors("name")) {
                nameButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        nameButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the name of the product?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    product.setName(input);
                    Menus.PRODUCT_EDIT.open(player, product);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(nameButton, 13);

        // Description button
        // ===========================
        MenuItemBuilder descriptionButton = new MenuItemBuilder(Material.PAPER);

        descriptionButton.setName(UI.color("Description", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (product.getDescription().length() > 0) {
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                descriptionButton.addLore(UI.color(line, UI.COLOR_SECONDARY));
            }
        } else {
            descriptionButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        descriptionButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (product.hasErrors("description")) {
            descriptionButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            descriptionButton.addEnchantGlow();

            for (String error : product.getErrors("description")) {
                descriptionButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        descriptionButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the description of the product?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    product.setDescription(input);
                    Menus.PRODUCT_EDIT.open(player, product);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(descriptionButton, 15);

        // Category button
        // ===========================
        MenuItemBuilder categoryButton = new MenuItemBuilder(Material.CHEST_MINECART);

        categoryButton.setName(UI.color("Category", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (product.getCategory() != null) {
            categoryButton.addLore(UI.color(product.getCategory().getName(), UI.COLOR_SECONDARY));
        } else {
            categoryButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        categoryButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (product.hasErrors("category")) {
            categoryButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            categoryButton.addEnchantGlow();

            for (String error : product.getErrors("category")) {
                categoryButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        categoryButton.setClickListener(event -> {
            CategoryService categoryService = Commerce.getInstance().getService("categories");

            UI.playSound(player, UI.SOUND_CLICK);
            categoryButton.setLore(UI.color("Loading...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();

            // Create menu
            categoryService.getCategories(categories -> {
                menu.enableButtons();
                
                if (categories == null) {
                    UI.playSound(player, UI.SOUND_ERROR);
                    categoryButton.setLore(UI.color("Error: Failed to load categories.", UI.COLOR_ERROR));
                    menu.refresh();
                    return;
                }

                Menus.PRODUCT_CATEGORY.open(player, product, categories);
            });
        });

        menu.setButton(categoryButton, 29);

        // Price button
        // ===========================
        MenuItemBuilder priceButton = new MenuItemBuilder(Material.SUNFLOWER);

        priceButton.setName(UI.color("Price", UI.COLOR_PRIMARY, ChatColor.BOLD));
        priceButton.setLore(UI.color(ShopHelper.formatPrice(product.getPrice()), UI.COLOR_SECONDARY));

        priceButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        
        // Add validation errors to lore
        if (product.hasErrors("price")) {
            priceButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            priceButton.addEnchantGlow();

            for (String error : product.getErrors("price")) {
                priceButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        priceButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the price of the product?", UI.COLOR_PRIMARY);
                }

                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    product.setPrice(input.floatValue());
                    Menus.PRODUCT_EDIT.open(player, product);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(priceButton, 31);

        // Commands button
        // ===========================
        MenuItemBuilder commandsButton = new MenuItemBuilder(Material.COMMAND_BLOCK);

        commandsButton.setName(UI.color("Commands", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (product.getCommands().size() > 0) {
            for (Command command : product.getCommands()) {
                commandsButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command.getCommand(), UI.COLOR_SECONDARY));
            }
        } else {
            commandsButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        commandsButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        commandsButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_COMMANDS.open(player, product);
        });

        menu.setButton(commandsButton, 33);

        // Set bottom line
        // ===========================
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(new ActionProductList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        if (product.hasErrors()) {
            saveButton.setLore(UI.color("Error: The glowing fields have invalid values.", UI.COLOR_ERROR));
        }

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();

            // Save product
            productService.saveProduct(product, success -> {
                menu.enableButtons();
                
                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    new ActionProductList(false).onClick(event);
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    Menus.PRODUCT_EDIT.open(player, product);
                }
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
