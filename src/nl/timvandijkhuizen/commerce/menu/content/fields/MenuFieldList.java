package nl.timvandijkhuizen.commerce.menu.content.fields;

import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuFieldList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        FieldService fieldService = Commerce.getInstance().getService("fields");
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Fields", 3, 7, 1, 1);

        // Add field buttons
        Set<Field> fields = args.get(0);

        for (Field field : fields) {
            MenuItemBuilder item = new MenuItemBuilder(field.getIcon());

            // Set field name
            item.setName(UI.color(field.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(field.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }

            item.addLore("", UI.color("Type: ", UI.COLOR_TEXT) + UI.color(field.getType().getDisplayName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Required: ", UI.COLOR_TEXT) + UI.color(field.isRequired() ? "Yes" : "No", UI.COLOR_SECONDARY), "");

            item.addLore("", UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.SOUND_CLICK);

                if (clickType == ClickType.LEFT) {
                    Menus.FIELD_EDIT.open(player, field);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.COLOR_TEXT));
                    menu.refresh();

                    fieldService.deleteField(field, success -> {
                        if (success) {
                            UI.playSound(player, UI.SOUND_DELETE);
                            menu.removePagedItem(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete field.", UI.COLOR_ERROR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new field button
        MenuItemBuilder createButton = new MenuItemBuilder(XMaterial.NETHER_STAR);

        createButton.setName(UI.color("Create Field", UI.COLOR_SECONDARY, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_EDIT.open(player);
        });

        menu.setItem(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
