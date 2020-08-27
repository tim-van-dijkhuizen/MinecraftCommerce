package nl.timvandijkhuizen.custompayments.menu.content.products;

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
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.PriceHelper;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenProductList;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        ProductService productService = CustomPayments.getInstance().getService("products");
        Product product = args.length == 1 ? args[0].as(Product.class) : new Product();
        Menu menu = new Menu((product.getId() != null ? "Edit" : "Create") + " Product", MenuSize.XXL);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder(product.getIcon());

        iconButton.setName(UI.color("Icon", UI.PRIMARY_COLOR, ChatColor.BOLD));

        // Add validation errors to lore
        if (product.hasErrors("icon")) {
            iconButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            iconButton.addEnchantGlow();

            for (String error : product.getErrors("icon")) {
                iconButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }
        
        iconButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.PRODUCT_ICON.open(player, product, product.getIcon());
        });

        menu.setButton(iconButton, 11);

        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(Material.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (product.getName().length() > 0) {
            nameButton.addLore(UI.color(product.getName(), UI.SECONDARY_COLOR));
        } else {
            nameButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        nameButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (product.hasErrors("name")) {
            nameButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            nameButton.addEnchantGlow();

            for (String error : product.getErrors("name")) {
                nameButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        nameButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

            UI.playSound(player, UI.CLICK_SOUND);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the name of the product?", UI.PRIMARY_COLOR);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    product.setName(input);
                    Menus.PRODUCT_EDIT.open(player, product);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            player.closeInventory();
            conversation.begin();
        });

        menu.setButton(nameButton, 13);

        // Description button
        // ===========================
        MenuItemBuilder descriptionButton = new MenuItemBuilder(Material.PAPER);

        descriptionButton.setName(UI.color("Description", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (product.getDescription().length() > 0) {
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                descriptionButton.addLore(UI.color(line, UI.SECONDARY_COLOR));
            }
        } else {
            descriptionButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        descriptionButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (product.hasErrors("description")) {
            descriptionButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            descriptionButton.addEnchantGlow();

            for (String error : product.getErrors("description")) {
                descriptionButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        descriptionButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

            UI.playSound(player, UI.CLICK_SOUND);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the description of the product?", UI.PRIMARY_COLOR);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    product.setDescription(input);
                    Menus.PRODUCT_EDIT.open(player, product);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            player.closeInventory();
            conversation.begin();
        });

        menu.setButton(descriptionButton, 15);

        // Category button
        // ===========================
        MenuItemBuilder categoryButton = new MenuItemBuilder(Material.CHEST_MINECART);

        categoryButton.setName(UI.color("Category", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (product.getCategory() != null) {
            categoryButton.addLore(UI.color(product.getCategory().getName(), UI.SECONDARY_COLOR));
        } else {
            categoryButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        categoryButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (product.hasErrors("category")) {
            categoryButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            categoryButton.addEnchantGlow();

            for (String error : product.getErrors("category")) {
                categoryButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        categoryButton.setClickListener(event -> {
            CategoryService categoryService = CustomPayments.getInstance().getService("categories");

            UI.playSound(player, UI.CLICK_SOUND);
            categoryButton.setLore(UI.color("Loading...", UI.TEXT_COLOR));
            menu.refresh();

            // Create menu
            categoryService.getCategories(categories -> {
                if (categories == null) {
                    UI.playSound(player, UI.ERROR_SOUND);
                    categoryButton.setLore(UI.color("Error: Failed to load categories.", UI.ERROR_COLOR));
                    menu.refresh();
                }

                Menus.PRODUCT_CATEGORY.open(player, product, categories, product.getCategory());
            });
        });

        menu.setButton(categoryButton, 29);

        // Price button
        // ===========================
        MenuItemBuilder priceButton = new MenuItemBuilder(Material.SUNFLOWER);

        priceButton.setName(UI.color("Price", UI.PRIMARY_COLOR, ChatColor.BOLD));
        priceButton.setLore(UI.color(PriceHelper.format(product.getPrice()), UI.SECONDARY_COLOR));

        priceButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        
        // Add validation errors to lore
        if (product.hasErrors("price")) {
            priceButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            priceButton.addEnchantGlow();

            for (String error : product.getErrors("price")) {
                priceButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        priceButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

            UI.playSound(player, UI.CLICK_SOUND);

            Conversation conversation = factory.withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the price of the product?", UI.PRIMARY_COLOR);
                }

                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    product.setPrice(input.floatValue());
                    Menus.PRODUCT_EDIT.open(player, product);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            player.closeInventory();
            conversation.begin();
        });

        menu.setButton(priceButton, 31);

        // Commands button
        // ===========================
        MenuItemBuilder commandsButton = new MenuItemBuilder(Material.COMMAND_BLOCK);

        commandsButton.setName(UI.color("Commands", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (product.getCommands().size() > 0) {
            for (Command command : product.getCommands()) {
                commandsButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command.getCommand(), UI.SECONDARY_COLOR));
            }
        } else {
            commandsButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        commandsButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        commandsButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.PRODUCT_COMMANDS.open(player, product);
        });

        menu.setButton(commandsButton, 33);

        // Set bottom line
        // ===========================
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 4);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = Menu.CANCEL_BUTTON.clone();

        cancelButton.setClickListener(new OpenProductList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = Menu.SAVE_BUTTON.clone();

        if (product.hasErrors()) {
            saveButton.setLore(UI.color("Error: The glowing fields have invalid values.", UI.ERROR_COLOR));
        }

        saveButton.setClickListener(event -> {
            ClickType clickType = event.getClickType();

            UI.playSound(player, UI.CLICK_SOUND);
            saveButton.setLore(UI.color("Saving...", UI.TEXT_COLOR));
            menu.refresh();

            // Save product
            productService.saveProduct(product, success -> {
                if (success) {
                    OpenProductList action = new OpenProductList(false);
                    
                    UI.playSound(player, UI.SUCCESS_SOUND);
                    action.onClick(new MenuItemClick(player, menu, saveButton, clickType));
                } else {
                    UI.playSound(player, UI.ERROR_SOUND);
                    Menus.PRODUCT_EDIT.open(player, product);
                }
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
