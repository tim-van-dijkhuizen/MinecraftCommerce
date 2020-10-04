package nl.timvandijkhuizen.commerce.menu.content.fields;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionFieldList;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuFieldEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        FieldService fieldService = Commerce.getInstance().getService("fields");
        Field field = args.get(0, new Field());
        Menu menu = new Menu((field.getId() != null ? "Edit" : "Create") + " Field", MenuSize.XXL);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder(field.getIcon());

        iconButton.setName(UI.color("Icon", UI.COLOR_PRIMARY, ChatColor.BOLD));

        // Add validation errors to lore
        if (field.hasErrors("icon")) {
            iconButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            iconButton.addEnchantGlow();

            for (String error : field.getErrors("icon")) {
                iconButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }
        
        iconButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_ICON.open(player, field, field.getIcon());
        });

        menu.setButton(iconButton, 11);
        
        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(Material.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (field.getName().length() > 0) {
            nameButton.addLore(UI.color(field.getName(), UI.COLOR_SECONDARY));
        } else {
            nameButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }

        nameButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        
        // Add validation errors to lore
        if (field.hasErrors("name")) {
            nameButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            nameButton.addEnchantGlow();

            for (String error : field.getErrors("name")) {
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
                    return UI.color("What should be the name of the field?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    field.setName(input);
                    Menus.FIELD_EDIT.open(player, field);
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

        if (field.getDescription().length() > 0) {
            String[] lines = WordUtils.wrap(field.getDescription(), 40).split("\n");

            for (String line : lines) {
                descriptionButton.addLore(UI.color(line, UI.COLOR_SECONDARY));
            }
        } else {
            descriptionButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        descriptionButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (field.hasErrors("description")) {
            descriptionButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            descriptionButton.addEnchantGlow();

            for (String error : field.getErrors("description")) {
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
                    return UI.color("What should be the description of the field?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    field.setDescription(input);
                    Menus.FIELD_EDIT.open(player, field);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(descriptionButton, 15);
        
        // Type button
        // ===========================
        MenuItemBuilder typeButton = new MenuItemBuilder(Material.CAULDRON);

        typeButton.setName(UI.color("Type", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (field.getType() != null) {
            typeButton.addLore(UI.color(field.getType().getName(), UI.COLOR_SECONDARY));
        } else {
            typeButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        typeButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (field.hasErrors("type")) {
            typeButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            typeButton.addEnchantGlow();

            for (String error : field.getErrors("type")) {
                typeButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        typeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_TYPE.open(player, field);
        });

        menu.setButton(typeButton, 30);
        
        // Required button
        // ===========================
        Material requiredIcon = field.isRequired() ? Material.LIME_TERRACOTTA : Material.LIGHT_GRAY_TERRACOTTA;
        MenuItemBuilder requiredButton = new MenuItemBuilder(requiredIcon);

        requiredButton.setName(UI.color("Required", UI.COLOR_PRIMARY, ChatColor.BOLD));
        requiredButton.setLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Set click listener
        requiredButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            
            // Invert boolean value
            field.setRequired(!field.isRequired());
            
            // Update button material
            if(field.isRequired()) {
                requiredButton.setType(Material.LIME_TERRACOTTA);
            } else {
                requiredButton.setType(Material.LIGHT_GRAY_TERRACOTTA);
            }
            
            menu.refresh();
        });

        menu.setButton(requiredButton, 32);

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

        cancelButton.setClickListener(new ActionFieldList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        if (field.hasErrors()) {
            saveButton.setLore(UI.color("Error: The glowing fields have invalid values.", UI.COLOR_ERROR));
        }

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();

            // Save field
            fieldService.saveField(field, success -> {
                menu.enableButtons();
                
                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    new ActionFieldList(false).onClick(event);
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    Menus.FIELD_EDIT.open(player, field);
                }
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
