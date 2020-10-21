package nl.timvandijkhuizen.commerce.menu.content.fields;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuFieldIcon implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Field Icon", 3, 7, 1, 1, 1, 5, 7);
        Field field = args.get(0);
        Material selected = args.get(1);

        for (Material icon : Commerce.MATERIAL_ICONS) {
            MenuItemBuilder item = new MenuItemBuilder(icon);

            item.setName(UI.color(WordUtils.capitalize(icon.name().replace('_', ' ').toLowerCase()), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (icon == selected) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                field.setIcon(icon);
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.FIELD_EDIT.open(player, field);
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.FIELD_EDIT.open(player, field);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}