package nl.timvandijkhuizen.commerce.helpers;

import java.util.List;

import org.bukkit.ChatColor;

import nl.timvandijkhuizen.commerce.base.Model;
import nl.timvandijkhuizen.spigotutils.inventory.ItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ValidationHelper {

    public static void addErrorLore(ItemBuilder item, Model model, String property) {
        List<String> lore = item.getLore();
        
        // Add errors to existing lore
        addErrorLore(lore, model, property);
        
        item.setLore(lore);
    }
    
    public static void addErrorLore(List<String> lore, Model model, String property) {
        if (model.hasErrors(property)) {
            lore.add("");
            lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));

            for (String error : model.getErrors(property)) {
                lore.add(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
            }
        }
    }
    
}
