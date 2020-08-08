package nl.timvandijkhuizen.custompayments.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;

public class CommandProduct extends BaseCommand {

	@Override
	public String getCommand() {
		return "products";
	}
	
	@Override
	public String getUsage() {
		return "/custompayments products";
	}
	
	@Override
	public void onPlayerUse(Player player, String[] args) throws Exception {
		Storage storage = CustomPayments.getInstance().getStorage();
		MenuService menuService = CustomPayments.getInstance().getService("menus");
		
		// Create menu
		List<Product> products = storage.getProducts(null);
		PagedMenu menu = new PagedMenu("Products", 3, 7, 1, 1);
		
		for(Product product : products) {
			MenuItemBuilder item = new MenuItemBuilder(Material.DIAMOND);
			
			item.setName(product.getName());
			item.setLore(product.getDescription());
			
			menu.addPagedButton(item);
		}
		
		menuService.openMenu(player, menu);
	}

	@Override
	public void onConsoleUse(CommandSender console, String[] args) throws Exception {
		
	}

}
