package nl.timvandijkhuizen.commerce.config.sources;

import java.util.Collection;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.services.UserService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.JsonConfig;

public class UserPreferences extends JsonConfig {

    public UserPreferences(JsonObject json) {
        super(json);
    }

    public UserPreferences() {
        super();
    }

    @Override
    public void addOption(ConfigOption<?> option) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addOptions(Collection<ConfigOption<?>> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ConfigOption<?>> getOptions() {
        UserService userService = Commerce.getInstance().getService(UserService.class);
        return userService.getUserOptions();
    }

}
