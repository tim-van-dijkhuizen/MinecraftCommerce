package nl.timvandijkhuizen.custompayments.menu.content.products;

import java.util.stream.Stream;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductIcon implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataValue... args) {
		Product product = args[0].as(Product.class);
		PagedMenu menu = new PagedMenu("Product icon", 3, 7, 1, 1);
		
		// Add icon buttons
		Material[] icons = Stream.of(Material.values()).filter(icon -> icon.isItem() && !icon.isAir()).toArray(Material[]::new);
		
		for(Material icon : icons) {
			MenuItemBuilder item = new MenuItemBuilder(icon);
			
			item.setName(UI.color(WordUtils.capitalize(icon.name().replace('_', ' ').toLowerCase()), UI.PRIMARY_COLOR, ChatColor.BOLD));
			
			item.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
				product.setIcon(icon);
				whoClicked.playSound(whoClicked.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
				Menus.PRODUCT_EDIT.open(player, product);
			});
			
			menu.addPagedButton(item);
		}
		
		// Cancel button
		MenuItemBuilder cancelButton = Menu.CANCEL_BUTTON.clone();
		
		cancelButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			Menus.PRODUCT_EDIT.open(player, product);
		});
		
		menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 4);
		
		return menu;
	}

}