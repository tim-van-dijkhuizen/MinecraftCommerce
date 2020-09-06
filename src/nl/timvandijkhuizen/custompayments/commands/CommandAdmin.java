package nl.timvandijkhuizen.custompayments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.commands.admin.CommandCategories;
import nl.timvandijkhuizen.custompayments.commands.admin.CommandConfig;
import nl.timvandijkhuizen.custompayments.commands.admin.CommandGateways;
import nl.timvandijkhuizen.custompayments.commands.admin.CommandOrders;
import nl.timvandijkhuizen.custompayments.commands.admin.CommandProducts;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandAdmin extends BaseCommand {

    @Override
    public String getCommand() {
        return "admin";
    }

    @Override
    public String getUsage() {
        return "/custompayments admin";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        Menus.HOME.open(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }
    
    public BaseCommand[] getSubCommands() {
        return new BaseCommand[] {
            new CommandConfig(),
            new CommandCategories(),
            new CommandProducts(),
            new CommandGateways(),
            new CommandOrders()
        };
    }

}
