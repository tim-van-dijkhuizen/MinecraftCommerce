package nl.timvandijkhuizen.custompayments.menu.content.products;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductCategory implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataValue... args) {
		Product product = args[0].as(Product.class);
		List<Category> categories = args[1].asList(Category.class);
		Category selected = args[2].as(Category.class);
		PagedMenu menu = new PagedMenu("Product Category", 3, 7, 1, 1);
		
		// Add category buttons
		for(Category category : categories) {
			MenuItemBuilder item = new MenuItemBuilder(Material.CHEST_MINECART);
			
			item.setName(UI.color(category.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));
			
			// Enchant if selected
			if(selected != null && category.getId() == selected.getId()) {
				item.addEnchantGlow();
			}
			
			item.setClickListener(event -> {
				product.setCategory(category);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
				Menus.PRODUCT_EDIT.open(player, product);
			});
			
			menu.addPagedButton(item);
		}
		
		// Cancel button
		MenuItemBuilder cancelButton = Menu.CANCEL_BUTTON.clone();
		
		cancelButton.setClickListener(event -> {
			player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			Menus.PRODUCT_EDIT.open(player, product);
		});
		
		menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 4);
		
		return menu;
	}

}