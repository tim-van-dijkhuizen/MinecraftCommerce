package nl.timvandijkhuizen.custompayments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

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
		ProductService productService = CustomPayments.getInstance().getService("products");
		
		player.sendMessage(UI.color("Loading...",UI.TEXT_COLOR));
		
		// Create menu
		productService.getProducts(null, products -> {
			if(products == null) {
				showError("Failed to load products.");
			}
			
			Menus.PRODUCT_LIST.open(player, products);
		});
	}

	@Override
	public void onConsoleUse(CommandSender console, String[] args) throws Exception {
		console.sendMessage("");
	}

}
