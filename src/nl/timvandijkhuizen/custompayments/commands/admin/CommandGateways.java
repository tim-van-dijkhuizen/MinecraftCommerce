package nl.timvandijkhuizen.custompayments.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandGateways extends BaseCommand {

    @Override
    public String getCommand() {
        return "gateways";
    }

    @Override
    public String getUsage() {
        return "/custompayments admin gateways";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");

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