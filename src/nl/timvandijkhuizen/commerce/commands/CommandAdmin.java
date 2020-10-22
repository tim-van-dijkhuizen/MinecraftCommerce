package nl.timvandijkhuizen.commerce.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandAdmin extends BaseCommand {

    @Override
    public String getCommand() {
        return "admin";
    }

    @Override
    public String getUsage() {
        return "/commerce admin";
    }

    @Override
    public String getPermission() {
        return "commerce.admin";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Throwable {
        Menus.HOME.open(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Throwable {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

}
