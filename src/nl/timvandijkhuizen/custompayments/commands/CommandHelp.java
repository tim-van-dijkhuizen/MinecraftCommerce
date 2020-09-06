package nl.timvandijkhuizen.custompayments.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandHelp extends BaseCommand {

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "/custompayments help";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        showAvailableCommands(player);
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        showAvailableCommands(console);
    }
    
    private void showAvailableCommands(CommandSender sender) {
        CommandService commandService = CustomPayments.getInstance().getService("commands");
        
        sender.sendMessage(UI.color("Available commands:", UI.COLOR_PRIMARY));
        
        for(BaseCommand command : commandService.getCommands()) {
            showCommandUsage(sender, command, 1);
        }
    }
    
    private void showCommandUsage(CommandSender sender, BaseCommand command, int indentLevel) {
        if(command.getPermission() == null || sender.hasPermission(command.getPermission())) {
            String prefix = StringUtils.repeat(UI.TAB, indentLevel) + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + " ";
            sender.sendMessage(prefix + UI.color(command.getUsage(), UI.COLOR_SECONDARY));
            
            // Increase indentation level
            indentLevel++;
            
            // Show subcommands
            for(BaseCommand subCommand : command.getSubCommands()) {
                showCommandUsage(sender, subCommand, indentLevel);
            }
        }
    }

}
