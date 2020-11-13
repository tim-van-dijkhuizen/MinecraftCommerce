package nl.timvandijkhuizen.commerce.menu.content.fields;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.helpers.ValidationHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionFieldList;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.InputHelper;
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
        Menu menu = new Menu("Admin " + Icon.ARROW_RIGHT + " " + (field.getId() != null ? "Edit" : "Create") + " Field", MenuSize.XXL);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder();

        iconButton.setTypeGenerator(() -> field.getIcon());
        iconButton.setName(UI.color("Icon", UI.COLOR_PRIMARY, ChatColor.BOLD));

        iconButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            ValidationHelper.addErrorLore(lore, field, "icon");

            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_ICON.open(player, field, menu);
        });

        menu.setItem(iconButton, 11);

        // Handle button
        // ===========================
        MenuItemBuilder handleButton = new MenuItemBuilder(XMaterial.LEVER);

        handleButton.setName(UI.color("Handle", UI.COLOR_PRIMARY, ChatColor.BOLD));

        handleButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (field.getHandle().length() > 0) {
                lore.add(UI.color(field.getHandle(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, field, "handle");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        handleButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);

            InputHelper.getString(player, UI.color("What should be the handle of the field?", UI.COLOR_PRIMARY), (ctx, value) -> {
                field.setHandle(value);
                menu.open(player);
                return null;
            });
        });

        menu.setItem(handleButton, 13);
        
        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(XMaterial.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        nameButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (field.getName().length() > 0) {
                lore.add(UI.color(field.getName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, field, "name");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        nameButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);

            InputHelper.getString(player, UI.color("What should be the name of the field?", UI.COLOR_PRIMARY), (ctx, value) -> {
                field.setName(value);
                menu.open(player);
                return null;
            });
        });

        menu.setItem(nameButton, 15);

        // Description button
        // ===========================
        MenuItemBuilder descriptionButton = new MenuItemBuilder(XMaterial.PAPER);

        descriptionButton.setName(UI.color("Description", UI.COLOR_PRIMARY, ChatColor.BOLD));

        descriptionButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (field.getDescription().length() > 0) {
                String[] lines = WordUtils.wrap(field.getDescription(), 40).split("\n");

                for (String line : lines) {
                    lore.add(UI.color(line, UI.COLOR_TEXT));
                }
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }
            
            ValidationHelper.addErrorLore(lore, field, "description");

            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        descriptionButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);

            InputHelper.getString(player, UI.color("What should be the description of the field?", UI.COLOR_PRIMARY), (ctx, value) -> {
                field.setDescription(value);
                menu.open(player);
                return null;
            });
        });

        menu.setItem(descriptionButton, 29);

        // Type button
        // ===========================
        MenuItemBuilder typeButton = new MenuItemBuilder(XMaterial.CAULDRON);

        typeButton.setName(UI.color("Type", UI.COLOR_PRIMARY, ChatColor.BOLD));

        typeButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (field.getType() != null) {
                lore.add(UI.color(field.getType().getDisplayName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, field, "type");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        typeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_TYPE.open(player, field, menu);
        });

        menu.setItem(typeButton, 31);

        // Required button
        // ===========================
        MenuItemBuilder requiredButton = new MenuItemBuilder();

        requiredButton.setTypeGenerator(() -> {
            return field.isRequired() ? XMaterial.LIME_TERRACOTTA.parseMaterial() : XMaterial.LIGHT_GRAY_TERRACOTTA.parseMaterial();
        });
        
        requiredButton.setName(UI.color("Required", UI.COLOR_PRIMARY, ChatColor.BOLD));
        requiredButton.setLore("", UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Set click listener
        requiredButton.setClickListener(event -> {
            field.setRequired(!field.isRequired());
            UI.playSound(player, UI.SOUND_CLICK);
            menu.refresh();
        });

        menu.setItem(requiredButton, 33);

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

        cancelButton.setClickListener(new ActionFieldList());

        menu.setItem(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableItems();
            menu.refresh();

            // Save field
            fieldService.saveField(field, success -> {
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
