package nl.timvandijkhuizen.custompayments.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandProducts extends BaseCommand {

    @Override
    public String getCommand() {
        return "products";
    }

    @Override
    public String getUsage() {
        return "/custompayments admin products";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        ProductService productService = CustomPayments.getInstance().getService("products");

        player.sendMessage(UI.color("Loading...", UI.TEXT_COLOR));

        // Create menu
        productService.getProducts(products -> {
            if (products == null) {
                player.sendMessage(UI.color("Failed to load products.", UI.ERROR_COLOR));
                return;
            }

            Menus.PRODUCT_LIST.open(player, products);
        });
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.ERROR_COLOR));
    }

}
