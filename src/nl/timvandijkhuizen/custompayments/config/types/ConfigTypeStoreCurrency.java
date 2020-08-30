package nl.timvandijkhuizen.custompayments.config.types;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.StoreCurrency;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeStoreCurrency implements ConfigType<StoreCurrency> {

    @Override
    public StoreCurrency getValue(OptionConfig config, ConfigOption<StoreCurrency> option) {
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);
        
        // Get store currency
        String code = config.getString(option.getPath());
        
        Optional<StoreCurrency> currency = currencies
            .stream()
            .filter(i -> i.getCode().equals(code))
            .findFirst();

        return currency.orElse(null);
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<StoreCurrency> option, StoreCurrency value) {
        config.set(option.getPath(), value.getCode());
    }

    @Override
    public StoreCurrency getValue(JsonObject json, ConfigOption<StoreCurrency> option) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setValue(JsonObject json, ConfigOption<StoreCurrency> option, StoreCurrency value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getValueLore(StoreCurrency value) {
        return new String[] { value.getCode() };
    }

    @Override
    public boolean isValueEmpty(StoreCurrency value) {
        return value == null;
    }

    @Override
    public void getValueInput(Player player, StoreCurrency value, Consumer<StoreCurrency> callback) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);
        PagedMenu menu = new PagedMenu("Select Currency", 3, 7, 1, 1);

        for (StoreCurrency currency : currencies) {
            MenuItemBuilder item = new MenuItemBuilder(Material.SUNFLOWER);

            item.setName(UI.color(currency.getCode(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            item.setClickListener(event -> {
                UI.playSound(player, UI.CLICK_SOUND);
                callback.accept(currency);
            });

            menu.addPagedButton(item);
        }
        
        menu.open(player);
    }

}
