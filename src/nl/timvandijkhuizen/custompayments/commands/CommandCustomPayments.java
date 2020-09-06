package nl.timvandijkhuizen.custompayments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

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
        CategoryService categoryService = CustomPayments.getInstance().getService("categories");

        player.sendMessage(UI.color("Loading...", UI.COLOR_TEXT));

        // Create menu
        categoryService.getCategories(categories -> {
            if (categories == null) {
                player.sendMessage(UI.color("Failed to load categories.", UI.COLOR_ERROR));
                return;
            }

            Menus.SHOP_CATEGORIES.open(player, categories);
        });
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
