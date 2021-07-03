package nl.timvandijkhuizen.commerce.config.types;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.helpers.InputHelper;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;

public class ConfigTypeTerms implements ConfigType<List<String>> {

    @Override
    public List<String> getValue(OptionConfig config, ConfigOption<List<String>> option) {
        return config.getStringList(option.getPath());
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<List<String>> option, List<String> value) {
        config.set(option.getPath(), value);
    }

    @Override
    public String getRawValue(OptionConfig config, ConfigOption<List<String>> option) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<List<String>> option) {
        return "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<List<String>> option) {
        boolean hasContent = false;
        
        // Check if there are any non-empty pages
        for(String page : getValue(config, option)) {
            if(page.length() > 0) {
                hasContent = true;
            }
        }
        
        return !hasContent;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<List<String>> option, MenuClick event, Consumer<List<String>> callback) {
        Player player = event.getPlayer();
        
        // Get input
        InputHelper.getText(player, value -> {
            List<String> current = getValue(config, option);
            
            if(value != null) {
                callback.accept(value);
            } else {
                callback.accept(current);
            }
        });
    }

}
