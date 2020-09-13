package nl.timvandijkhuizen.commerce.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.menu.Menus;
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
    public void onPlayerUse(Player player, String[] args) throws Exception {
        Menus.ORDER_LIST.open(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

}