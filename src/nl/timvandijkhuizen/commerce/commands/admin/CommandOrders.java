package nl.timvandijkhuizen.commerce.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandOrders extends BaseCommand {

    @Override
    public String getCommand() {
        return "orders";
    }

    @Override
    public String getUsage() {
        return "/commerce admin orders";
    }

    @Override
    public String getPermission() {
        return "commerce.admin";
    }
    
    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        OrderService orderService = Commerce.getInstance().getService("orders");

        player.sendMessage(UI.color("Loading...", UI.COLOR_TEXT));

        // Create menu
        orderService.getOrders(orders -> {
            if (orders == null) {
                player.sendMessage(UI.color("Failed to load orders.", UI.COLOR_ERROR));
                return;
            }

            Menus.ORDER_LIST.open(player, orders);
        });
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

}