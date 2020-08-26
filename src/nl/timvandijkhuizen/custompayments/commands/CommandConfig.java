package nl.timvandijkhuizen.custompayments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandConfig extends BaseCommand {

    @Override
    public String getCommand() {
        return "config";
    }

    @Override
    public String getUsage() {
        return "/custompayments config";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        Menus.CONFIG.open(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.ERROR_COLOR));
    }

}
