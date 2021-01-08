# API - Field types

Using field types you can control which data is entered into certain fields.

### Create the field type
```java
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.helpers.InputHelper;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class FieldTypeTest implements FieldType<String> {

    @Override
    public String getValue(OptionConfig config, ConfigOption<String> option) {
        return config.getString(option.getPath());
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<String> option, String value) {
        config.set(option.getPath(), value);
    }

    @Override
    public String getRawValue(OptionConfig config, ConfigOption<String> option) {
        return !isValueEmpty(config, option) ? getValue(config, option) : "";
    }
    
    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<String> option) {
        return getRawValue(config, option);
    }


    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<String> option) {
        String value = getValue(config, option);
        return value == null || value.length() == 0;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<String> option, MenuClick event, Consumer<String> callback) {
        Player player = event.getPlayer();
        Menu menu = event.getMenu();
        
        menu.close(player);

        InputHelper.getString(player, UI.color("What should be the new value?", UI.COLOR_PRIMARY), (ctx, input) -> {
            callback.accept(input);
            return null;
        });
    }

    @Override
    public String getHandle() {
        // This value must be globally unique, only one field type can use this.
        return "test";
    }

    @Override
    public String getDisplayName() {
        return "Test";
    }

}
```

### Register the field type
```java
import nl.timvandijkhuizen.commerce.CommerceApi;

@Override
public void onLoad() {
    CommerceApi.registerFieldType(new FieldTypeTest());
}
```