package nl.timvandijkhuizen.commerce.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandProducts extends BaseCommand {

    @Override
    public String getCommand() {
        return "products";
    }

    @Override
    public String getUsage() {
        return "/commerce admin products";
    }
    
    @Override
    public String getPermission() {
        return "commerce.admin";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        ProductService productService = Commerce.getInstance().getService("products");

        player.sendMessage(UI.color("Loading...", UI.COLOR_TEXT));

        // Create menu
        productService.getProducts(products -> {
            if (products == null) {
                player.sendMessage(UI.color("Failed to load products.", UI.COLOR_ERROR));
                return;
            }

            Menus.PRODUCT_LIST.open(player, products);
        });
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

}
