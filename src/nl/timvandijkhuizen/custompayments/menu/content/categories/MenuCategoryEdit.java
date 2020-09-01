package nl.timvandijkhuizen.custompayments.menu.content.categories;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenCategoryList;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuCategoryEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        CategoryService categoryService = CustomPayments.getInstance().getService("categories");
        Category category = args.length == 1 ? args[0].as(Category.class) : new Category();
        Menu menu = new Menu((category.getId() != null ? "Edit" : "Create") + " Category", MenuSize.LG);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder(category.getIcon());

        iconButton.setName(UI.color("Icon", UI.PRIMARY_COLOR, ChatColor.BOLD));

        // Add validation errors to lore
        if (category.hasErrors("icon")) {
            iconButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            iconButton.addEnchantGlow();

            for (String error : category.getErrors("icon")) {
                iconButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }
        
        iconButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.CATEGORY_ICON.open(player, category, category.getIcon());
        });

        menu.setButton(iconButton, 11);
        
        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(Material.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (category.getName().length() > 0) {
            nameButton.addLore(UI.color(category.getName(), UI.SECONDARY_COLOR));
        } else {
            nameButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }

        nameButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        
        // Add validation errors to lore
        if (category.hasErrors("name")) {
            nameButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            nameButton.addEnchantGlow();

            for (String error : category.getErrors("name")) {
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
                    return UI.color("What should be the name of the category?", UI.PRIMARY_COLOR);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    category.setName(input);
                    Menus.CATEGORY_EDIT.open(player, category);
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

        if (category.getDescription().length() > 0) {
            String[] lines = WordUtils.wrap(category.getDescription(), 40).split("\n");

            for (String line : lines) {
                descriptionButton.addLore(UI.color(line, UI.SECONDARY_COLOR));
            }
        } else {
            descriptionButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        descriptionButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (category.hasErrors("description")) {
            descriptionButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            descriptionButton.addEnchantGlow();

            for (String error : category.getErrors("description")) {
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
                    return UI.color("What should be the description of the category?", UI.PRIMARY_COLOR);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    category.setDescription(input);
                    Menus.CATEGORY_EDIT.open(player, category);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            player.closeInventory();
            conversation.begin();
        });

        menu.setButton(descriptionButton, 15);

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

        cancelButton.setClickListener(new OpenCategoryList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        if (category.hasErrors()) {
            saveButton.setLore(UI.color("Error: The glowing fields have invalid values.", UI.ERROR_COLOR));
        }

        saveButton.setClickListener(event -> {
            ClickType clickType = event.getClickType();

            UI.playSound(player, UI.CLICK_SOUND);
            saveButton.setLore(UI.color("Saving...", UI.TEXT_COLOR));
            menu.refresh();

            // Save category
            categoryService.saveCategory(category, success -> {
                if (success) {
                    OpenCategoryList action = new OpenCategoryList(false);
                    
                    UI.playSound(player, UI.SUCCESS_SOUND);
                    action.onClick(new MenuItemClick(player, menu, saveButton, clickType));
                } else {
                    UI.playSound(player, UI.ERROR_SOUND);
                    Menus.CATEGORY_EDIT.open(player, category);
                }
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
