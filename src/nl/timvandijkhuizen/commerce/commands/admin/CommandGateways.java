package nl.timvandijkhuizen.commerce.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandGateways extends BaseCommand {

    @Override
    public String getCommand() {
        return "gateways";
    }

    @Override
    public String getUsage() {
        return "/commerce admin gateways";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");

        player.sendMessage(UI.color("Loading...", UI.COLOR_TEXT));

        // Create menu
        gatewayService.getGateways(gateways -> {
            if (gateways == null) {
                player.sendMessage(UI.color("Failed to load gateways.", UI.COLOR_ERROR));
                return;
            }

            Menus.GATEWAY_LIST.open(player, gateways);
        });
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.COLOR_ERROR));
    }

}