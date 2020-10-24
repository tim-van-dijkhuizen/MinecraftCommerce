package nl.timvandijkhuizen.commerce.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandCommerce extends BaseCommand {

    @Override
    public String getCommand() {
        return "commerce";
    }

    @Override
    public String getUsage() {
        return "/commerce";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Throwable {
        CategoryService categoryService = Commerce.getInstance().getService("categories");
        OrderService orderService = Commerce.getInstance().getService("orders");

        player.sendMessage(UI.color("Loading...", UI.COLOR_TEXT));

        // Create menu
        categoryService.getCategories(categories -> {
            if (categories == null) {
                player.sendMessage(UI.color("Failed to load categories.", UI.COLOR_ERROR));
                return;
            }

            orderService.getCart(player, cart -> {
                if (cart == null) {
                    player.sendMessage(UI.color("Failed to load cart.", UI.COLOR_ERROR));
                    return;
                }

                Menus.SHOP_CATEGORIES.open(player, categories, cart);
            });
        });
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Throwable {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

    public BaseCommand[] getSubCommands() {
        return new BaseCommand[] {
            new CommandAccount(),
            new CommandAdmin()
        };
    }

}
