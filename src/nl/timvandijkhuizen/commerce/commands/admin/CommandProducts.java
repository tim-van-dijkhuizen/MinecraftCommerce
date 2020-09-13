package nl.timvandijkhuizen.commerce.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.menu.Menus;
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
    public void onPlayerUse(Player player, String[] args) throws Exception {
        Menus.PRODUCT_LIST.open(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

}
