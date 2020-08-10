package nl.timvandijkhuizen.custompayments.menu.content.products;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductList implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataValue... args) {
		PagedMenu menu = new PagedMenu("Products", 3, 7, 1, 1);
		
		// Add product buttons
		List<Product> products = args[0].asList(Product.class);
		
		for(Product product : products) {
			MenuItemBuilder item = new MenuItemBuilder(product.getIcon());
			
			// Set product name
			item.setName(UI.color(product.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));
			
			// Split lore into smaller lines
			String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");
			
			for(String line : lines) {
				item.addLoreLine(UI.color(line, UI.TEXT_COLOR));
			}
			
			menu.addPagedButton(item);
		}
		
		// Create new product button
		MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);
		
		createButton.setName(UI.color("Create product", UI.SECONDARY_COLOR, ChatColor.BOLD));
		createButton.setClickListener(whoClicked -> Menus.PRODUCT_CREATE.open(whoClicked));
		
		menu.setButton(createButton, menu.getSize().getSize() - 9 + 4);
		
		return menu;
	}

}
