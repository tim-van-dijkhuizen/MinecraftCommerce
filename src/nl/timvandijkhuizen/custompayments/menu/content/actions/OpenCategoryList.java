package nl.timvandijkhuizen.custompayments.menu.content.actions;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuAction;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OpenCategoryList implements MenuAction<Player, Menu, MenuItemBuilder, ClickType> {

	@Override
	public void onClick(Player whoClicked, Menu menu, MenuItemBuilder item, ClickType type) {
		CategoryService categoryService = CustomPayments.getInstance().getService("categories");
		
		whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
		item.setLore(UI.color("Loading...", UI.TEXT_COLOR));
		menu.setButton(item, 15);

		// Create menu
		categoryService.getCategories(categories -> {
			if(categories == null) {
				item.setLore(UI.color("Error: Failed to load categories.", UI.ERROR_COLOR));
				menu.setButton(item, 15);
			}
			
			Menus.CATEGORY_LIST.open(whoClicked, categories);
		});
	}
	
}
