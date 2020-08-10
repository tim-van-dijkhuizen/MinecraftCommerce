package nl.timvandijkhuizen.custompayments.menu.content.products;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;

public class MenuProductCreate implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataValue... args) {
		Menu menu = new Menu("Create product", MenuSize.MD);
		
		MenuItemBuilder testButton = new MenuItemBuilder(Material.CARROT);
		testButton.setName("Test");
		menu.setButton(testButton, 13);
		
		return menu;
	}

}
