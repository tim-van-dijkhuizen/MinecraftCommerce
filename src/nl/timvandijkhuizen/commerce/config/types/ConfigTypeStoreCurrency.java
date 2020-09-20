package nl.timvandijkhuizen.commerce.config.types;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeStoreCurrency implements ConfigType<StoreCurrency> {

    @Override
    public StoreCurrency getValue(OptionConfig config, ConfigOption<StoreCurrency> option) {
        YamlConfig pluginConfig = Commerce.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = pluginConfig.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(pluginConfig);
        
        // Get store currency
        String code = config.getString(option.getPath());
        
        if(code == null) {
            return null;
        }
        
        Optional<StoreCurrency> currency = currencies.stream()
            .filter(i -> i.getCode().equals(code))
            .findFirst();

        return currency.orElse(null);
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<StoreCurrency> option, StoreCurrency value) {
        config.set(option.getPath(), value.getCode());
    }

    @Override
    public String getValueLore(OptionConfig config, ConfigOption<StoreCurrency> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getCode() : "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<StoreCurrency> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<StoreCurrency> option, Player player, Consumer<StoreCurrency> callback) {
        PagedMenu menu = new PagedMenu("Select Currency", 3, 7, 1, 1);
        StoreCurrency selected = getValue(config, option);

        // Get available currencies
        YamlConfig pluginConfig = Commerce.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = pluginConfig.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(pluginConfig);
        
        for (StoreCurrency currency : currencies) {
            MenuItemBuilder item = new MenuItemBuilder(Material.SUNFLOWER);

            item.setName(UI.color(currency.getCode(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.setLore(UI.color("Conversion rate: ", UI.COLOR_TEXT) + UI.color("" + currency.getConversionRate(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Format: ", UI.COLOR_TEXT) + UI.color(currency.getFormat().toPattern(), UI.COLOR_SECONDARY));

            if(selected != null && selected.getCode().equals(currency.getCode())) {
                item.addEnchantGlow();
            }
            
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(currency);
            });

            menu.addPagedButton(item);
        }
        
        menu.open(player);
    }

}
