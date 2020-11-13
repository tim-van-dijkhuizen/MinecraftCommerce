package nl.timvandijkhuizen.commerce.menu.content.fields;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuFieldType implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        FieldService fieldService = Commerce.getInstance().getService("fields");
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Field Type", 3, 7, 1, 1, 1, 5, 7);
        
        Field field = args.get(0);
        Menu returnMenu = args.get(1);
        FieldType<?> selected = field.getType();

        for (FieldType<?> type : fieldService.getFieldTypes()) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.CAULDRON);

            item.setName(UI.color(type.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (selected != null && type.getHandle().equals(selected.getHandle())) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                field.setType(type);
                UI.playSound(player, UI.SOUND_CLICK);
                returnMenu.open(player);
            });

            menu.addPagedItem(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            returnMenu.open(player);
        });

        menu.setItem(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}