package nl.timvandijkhuizen.commerce.config.types;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeInteger;
import nl.timvandijkhuizen.spigotutils.helpers.InputHelper;
import nl.timvandijkhuizen.spigotutils.input.InvalidInputException;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypePort extends ConfigTypeInteger {

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<Integer> option, MenuClick event, Consumer<Integer> callback) {
        Player player = event.getPlayer();
        Menu menu = event.getMenu();

        menu.close(player);
        
        InputHelper.getNumber(player, UI.color("What should be the new value?", UI.COLOR_PRIMARY), (ctx, input) -> {
            int value = input.intValue();

            // Check range
            if (value < 1 || value > Short.MAX_VALUE) {
                throw new InvalidInputException("Invalid port, choose a port between 1 and " + Short.MAX_VALUE + ".");
            }

            // Check server port
            if (value == Bukkit.getServer().getPort()) {
                throw new InvalidInputException("This port is already being used by your server.");
            }

            callback.accept(value);
            return null;    
        });
    }

}
