package nl.timvandijkhuizen.custompayments.menu;

import java.util.stream.Stream;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductCreate;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductList;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;

public enum Menus {
	
	PRODUCT_LIST(new MenuProductList()),
	PRODUCT_CREATE(new MenuProductCreate());
	
	private PredefinedMenu predefinedMenu;
	
	Menus(PredefinedMenu predefinedMenu) {
		this.predefinedMenu = predefinedMenu;
	}
	
	public void open(Player player, Object... args) {
		MenuService menuService = CustomPayments.getInstance().getService("menus");
		Menu menu = predefinedMenu.create(player, Stream.of(args).map(obj -> new DataValue(obj)).toArray(DataValue[]::new));
		
		if(menu instanceof PagedMenu) {
			menuService.openMenu(player, (PagedMenu) menu);
		} else {
			menuService.openMenu(player, menu);
		}
	}
	
}
