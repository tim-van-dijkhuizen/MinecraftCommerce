package nl.timvandijkhuizen.commerce.config.types;

import java.util.List;
import java.util.function.Consumer;

import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
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
        return null;
    }

    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<List<String>> option) {
        return getRawValue(config, option);
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<List<String>> option) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<List<String>> option, MenuClick event, Consumer<List<String>> callback) {
        // TODO Auto-generated method stub
        
    }

}
