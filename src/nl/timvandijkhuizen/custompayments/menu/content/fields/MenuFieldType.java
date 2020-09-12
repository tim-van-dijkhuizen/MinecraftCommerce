package nl.timvandijkhuizen.custompayments.menu.content.fields;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.FieldType;
import nl.timvandijkhuizen.custompayments.elements.Field;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.FieldService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuFieldType implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        FieldService fieldService = CustomPayments.getInstance().getService("fields");
        PagedMenu menu = new PagedMenu("Field Type", 3, 7, 1, 1, 1, 5, 7);
        Field field = args.get(0);
        FieldType<?> selected = field.getType();

        for (FieldType<?> type : fieldService.getFieldTypes()) {
            MenuItemBuilder item = new MenuItemBuilder(Material.CAULDRON);

            item.setName(UI.color(type.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (selected != null && type.getHandle().equals(selected.getHandle())) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                field.setType(type);
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