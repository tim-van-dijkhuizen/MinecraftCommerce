package nl.timvandijkhuizen.commerce.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.menu.Menus;
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
    public void onPlayerUse(Player player, String[] args) throws Exception {
        Menus.SHOP_CATEGORIES.open(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

    public BaseCommand[] getSubCommands() {
        return new BaseCommand[] {
            new CommandHelp(),
            new CommandAdmin()
        };
    }

}
