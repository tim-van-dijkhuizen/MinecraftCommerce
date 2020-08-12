package nl.timvandijkhuizen.custompayments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;

public class CommandCustomPayments extends BaseCommand {

	@Override
	public String getCommand() {
		return "custompayments";
	}
	
	@Override
	public String getUsage() {
		return "/custompayments";
	}
	
	@Override
	public void onPlayerUse(Player player, String[] args) throws Exception {
		Menus.HOME.open(player);
	}

	@Override
	public void onConsoleUse(CommandSender console, String[] args) throws Exception {
		
	}
	
	public BaseCommand[] getSubCommands() {
		return new BaseCommand[]{
			new CommandProduct(),
			new CommandCategory()
		};
	}

}
